package com.example.teamcity.ui;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.ui.pages.BuildsPage;
import com.example.teamcity.ui.pages.admin.CreateBuildConfigurationPage;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class CreateBuildTypeTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/engy-engy/workshops";

    @Test(description = "User should be able to create build type configuration", groups = {"Positive"})
    public void createBuildTypeConfigurationTest() {

        step("Login as user");
        loginAs(testData.getUser());

        step("Create project -> API");
        var project = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        step("Open Create Build Type Page (http://localhost:8111/admin/createObjectMenu.html?projectId={projectId}&showMode=createBuildTypeMenu)");
        CreateBuildConfigurationPage.open(testData.getProject().getId())
                        .createForm(REPO_URL)
                        .setupBuildTypeConfiguration(testData.getBuildType().getName());

        step("Check build type was successfully created with correct data on API level");
        var createdBuildTypeConfig = superUserCheckRequests.<BuildType>getRequest(BUILD_TYPES).read("name:" + testData.getBuildType().getName());
        softy.assertNotNull(createdBuildTypeConfig);

        step("Check that build type Check is visible in Project (http://localhost:8111/favorite/projects)");
        var buildsExist = BuildsPage.open(project.getId())
                .getBuilds().stream()
                .anyMatch(build -> build.getButton().text().equals(testData.getBuildType().getName()));
        softy.assertTrue(buildsExist);
    }
}
