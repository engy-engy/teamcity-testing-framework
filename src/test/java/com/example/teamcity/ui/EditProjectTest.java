package com.example.teamcity.ui;

import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.ui.pages.admin.EditProjectPage;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class EditProjectTest extends BaseUiTest {

    @Test(description = "User should not be able to delete project", groups = {"Positive"})
    public void userDeleteProjectTest() {
        step("Login as user");
        loginAs(testData.getUser());

        step("Create project");
        var project = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        step("Delete project");
        SelenideElement response = EditProjectPage.open(project.getId())
                .deleteProject();

        step("Check that delete project —> UI");
        softy.assertThat(response.text())
                .contains("Project \"%s\" has been moved to the \"config/_trash\" directory."
                        .formatted(testData.getProject().getName()));

        step("Check that delete project —> API");
        var checkedResponse = uncheckedSuperUser.getRequest(PROJECTS)
                .read(testData.getProject().getId())
                .then().statusCode(HttpStatus.SC_NOT_FOUND)
                .extract().response();
        softy.assertThat(checkedResponse.asString())
                .contains("No project found by name or internal/external id '%s'."
                        .formatted(testData.getProject().getId()));

    }

    @Test(description = "User should not be able to edit name", groups = {"Positive"})
    public void userEditProjectNameTest() {
        step("Login as user");
        loginAs(testData.getUser());

        step("Create project");
        var project = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        step("Edit name project");
        SelenideElement response = EditProjectPage.open(project.getId())
                .editNameProject(testData.getProject().getName());

        step("Check that name project —> UI");
        softy.assertThat(response.text()).contains("Your changes have been saved.");

        step("Check that name project —> API");
        var checkedResponse = uncheckedSuperUser.getRequest(PROJECTS)
                .read(testData.getProject().getId())
                .then().statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(checkedResponse.asString())
                .contains(testData.getProject().getName() + testData.getProject().getName());

    }

    @Test(description = "User should not be able to edit description", groups = {"Positive"})
    public void userEditProjectDescriptionTest() {
        step("Login as user");
        loginAs(testData.getUser());

        step("Create project");
        var project = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        step("Edit description project");
        SelenideElement response = EditProjectPage.open(project.getId())
                .editDescriptionProject(testData.getProject().getName());

        step("Check that description project —> UI");
        softy.assertThat(response.text()).contains("Your changes have been saved.");

        step("Check that description project —> API");
        var checkedResponse = uncheckedSuperUser.getRequest(PROJECTS)
                .read(testData.getProject().getId())
                .then().statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(checkedResponse.asString())
                .contains("Description: " + testData.getProject().getName());

    }

    @Test(description = "User should not be able to edit project id", groups = {"Positive"})
    public void userEditProjectIdProjectTest() {
        step("Login as user");
        loginAs(testData.getUser());

        step("Create project");
        var project = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        step("Edit description project");
        SelenideElement response = EditProjectPage.open(project.getId())
                .editProjectIdProject(testData.getProject().getId());

        step("Check that description project —> UI");
        softy.assertThat(response.text()).contains("Your changes have been saved.");

        step("Check that description project —> API");
        var checkedResponse = uncheckedSuperUser.getRequest(PROJECTS)
                .read(testData.getProject().getId())
                .then().statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(checkedResponse.asString())
                .contains(testData.getProject().getId() + testData.getProject().getId());

    }

    @Test(description = "User should not be able to edit project id with unsupported character ", groups = {"Negative"})
    public void userEditProjectIdWithUnsupportedCharacterProjectErrorTest() {
        step("Login as user");
        loginAs(testData.getUser());

        step("Create project");
        var project = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        step("Edit description project");
        SelenideElement response = EditProjectPage.open(project.getId())
                .editUnsupportedCharacterProjectIdProject(testData.getProject().getId());

        step("Check that description project —> UI");
        softy.assertThat(response.text()).contains(String.format(
                "Project ID \"%s %s\" is invalid: contains unsupported character ' '. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters).",
                testData.getProject().getId(), testData.getProject().getId()
        ));
    }
}
