package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.ui.pages.ProjectsPage;
import com.example.teamcity.ui.pages.admin.CreateProjectPage;
import com.example.teamcity.ui.pages.admin.ProjectPage;
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
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

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
        // Подготовка окружения
        step("Login as user");
        step("Check number of projects");

        // Взаимодействие с UI ()
        step("Open Create Project Page (http://localhost:8111/admin/createObjectMenu.html)");
        step("Send all project parameters (repository URL)");
        step("Click Proceed");
        step("Set Project Name value is empty");

        // Проверка состояния API (корректность отправки данных с клиента на API)
        step("Check that number of projects did not change");

        // Проверка состояния UI (корректность считывания данных и отображение данных на UI)
        step("Check that error appears `Project name must not be empty`");
    }
}
