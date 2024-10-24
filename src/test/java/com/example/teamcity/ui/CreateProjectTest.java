package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.ui.pages.ProjectsPage;
import com.example.teamcity.ui.pages.admin.CreateProjectPage;
import com.example.teamcity.ui.pages.admin.ProjectPage;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class CreateProjectTest extends BaseUiTest {

    private static final String REPO_URL = "https://github.com/engy-engy/teamcity-testing-framework";

    @Test(description = "User should be able to create project", groups = {"Positive"})
    public void userCreateProject() {
        // Подготовка окружения
        step("Login as user");
        loginAs(testData.getUser());

        // Взаимодействие с UI ()
        CreateProjectPage.open("_Root")
                .createForm(REPO_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        // Проверка состояния API (корректность отправки данных с клиента на API)
        step("Check that all entities (project, buildType) was successfully created with correct data on API level");
        var createdProject = superUserCheckRequests.<Project>getRequest(PROJECTS).read("name:" + testData.getProject().getName());
        softy.assertNotNull(createdProject);

        // Проверка состояния UI (корректность считывания данных и отображение данных на UI)
        step("Check that project is visible on Project Page (http://localhost:8111/favorite/projects)");
        ProjectPage.open(createdProject.getId())
                .title.shouldHave(Condition.exactText(testData.getProject().getName()));

        var projectExist = ProjectsPage.open()
                .getProjects().stream()
                .anyMatch(project -> project.getName().equals(testData.getProject().getName()));
        softy.assertTrue(projectExist);
    }

    @Test(description = "User should not be able to create project without name", groups = {"Negative"})
    public void userCreateProjectWithoutName() {
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
