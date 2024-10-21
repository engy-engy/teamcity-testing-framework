package com.example.teamcity.ui;

import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;
@Test(groups = {"Regression"})
public class CreateProjectTest extends BaseUiTest {
    @Test(description = "User should be able to create project", groups = {"Positive"})
    public void userCreateProject() {
        // Подготовка окружения
        step("Login as user");

        // Взаимодействие с UI ()
        step("Open Create Project Page (http://localhost:8111/admin/createObjectMenu.html)");
        step("Send all project parameters (repository URL)");
        step("Click Proceed");
        step("Fix Project name and Build Type name values");
        step("Click Proceed");

        // Проверка состояния API (корректность отправки данных с клиента на API)
        step("Check that all entities (project, buildType) was successfully created with correct data on API level");

        // Проверка состояния UI (корректность считывания данных и отображение данных на UI)
        step("Check that project is visible on Project Page (http://localhost:8111/favorite/projects)");
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
