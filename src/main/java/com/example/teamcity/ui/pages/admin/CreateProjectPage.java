package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;

public class CreateProjectPage extends CreateBasePage {
    private static final String PROJECT_SHOW_MODE = "createProjectMenu";

    private SelenideElement projectNameInput = $("#projectName");

    private SelenideElement progressLoader = $("#discoveryProgressContainer");

    private SelenideElement errorProjectName = $("#error_projectName");

    @Step("Open create project page")
    public static CreateProjectPage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, PROJECT_SHOW_MODE), CreateProjectPage.class);
    }

    public CreateProjectPage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public SelenideElement setupProject(String projectName, String buildTypeName, boolean waitForLoading) {
        projectNameInput.val(projectName);
        buildTypeNameInput.val(buildTypeName);
        proceedButton.click();

        if (waitForLoading) {
            progressLoader.shouldBe(attribute("style", "display: none;"));
            return null;
        }
        return errorProjectName;
    }
}
