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
}
