package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.pages.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class EditProjectPage extends BasePage {

    private static final String EDIT_PROJECT_PAGE_URL = "/admin/editProject.html?projectId=%s";

    private SelenideElement actionsButton = $(".btn_mini.popupLink");

    private SelenideElement deleteActionButton = $("a[title='Delete project']");

    private SelenideElement deleteProjectConfirmationInput = $("#deleteProject__hostnameConfirmation");

    private SelenideElement hostnamePlaceHolder = $(".hostnamePlaceholder");

    private SelenideElement deleteProjectButton = $("input[value='Delete']");

    private SelenideElement successMessage = $("#message_projectRemoved");

    @Step("Open create project edit page")
    public static EditProjectPage open(String projectId) {
        return Selenide.open(EDIT_PROJECT_PAGE_URL.formatted(projectId), EditProjectPage.class);
    }

    @Step("Delete project")
    public SelenideElement deleteProject() {
        var hostname = hostnamePlaceHolder.getText();
        actionsButton.shouldBe(Condition.visible).click();
        deleteActionButton.shouldBe(Condition.visible).click();
        if (deleteProjectConfirmationInput.is(Condition.visible)) {
            deleteProjectConfirmationInput.val(hostname);
            deleteProjectButton.shouldBe(Condition.visible).click();
        }
        return successMessage;
    }

}
