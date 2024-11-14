package com.example.teamcity.ui;

import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.ui.pages.ProjectsPage;
import com.example.teamcity.ui.pages.admin.CreateProjectPage;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class CreateProjectTest extends BaseUiTest {

    private static final String REPO_URL = "https://github.com/engy-engy/workshops";

    @Test(description = "User should be able to create project", groups = {"Positive"})
    public void userCreateProjectTest() {

        step("Login as user");
        loginAs(testData.getUser());

        CreateProjectPage.open("_Root")
                .createForm(REPO_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName(),true);

        step("Check that all entities (project, buildType) was successfully created with correct data on API level");
        var createdProject = superUserCheckRequests.<Project>getRequest(PROJECTS).read("name:" + testData.getProject().getName());
        softy.assertNotNull(createdProject);

        step("Check that project is visible on Project Page (http://localhost:8111/favorite/projects)");
        var projectExist = ProjectsPage.open()
                .getProjects().stream()
                .anyMatch(project -> project.getName().text().equals(testData.getProject().getName()));
        softy.assertTrue(projectExist);
        TestDataStorage.getStorage().addCreatedEntity(PROJECTS, createdProject);
    }

    @Test(description = "User should not be able to create project without name", groups = {"Negative"})
    public void userCreateProjectWithoutNameTest() {

        step("Login as user");
        loginAs(testData.getUser());

        step("Open Create Project Page (http://localhost:8111/admin/createObjectMenu.html)");
        SelenideElement errorElement = CreateProjectPage.open("_Root")
                .createForm(REPO_URL)
                .setupProject("", testData.getBuildType().getName(), false);

        step("Check that error appears `Project name must not be empty`");
        softy.assertEquals(errorElement.text(),"Project name must not be empty");

    }

    @Test(description = "User should not be able to create project with same name", groups = {"Negative"})
    public void userCreateProjectWithSameNameTest() {

        step("Login as user");
        loginAs(testData.getUser());

        var project = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        SelenideElement errorElement = CreateProjectPage.open("_Root")
                .createForm(REPO_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName(), false);

        softy.assertEquals(errorElement.text(),"Project with this name already exists: %s"
                .formatted(testData.getProject().getName(), project.getName()));
    }
}
