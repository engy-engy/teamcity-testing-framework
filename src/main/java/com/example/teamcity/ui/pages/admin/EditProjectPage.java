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

    private SelenideElement projectNameInput = $("#name");

    private SelenideElement projectIdInput = $("#externalId");

    private SelenideElement projectDescriptionInput = $("#description");

    private SelenideElement saveEditProjectButton = $("input.btn.btn_primary.submitButton[name='submitButton']");

    private SelenideElement messageEdit = $("#message_projectUpdated");

    private SelenideElement messageErrorEdit = $("#errorExternalId");

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

    @Step("Edit name project")
    public SelenideElement editNameProject(String name) {
        projectNameInput.val(name + name);
        saveEditProjectButton.click();
        return messageEdit.shouldBe(Condition.visible);
    }

    @Step("Edit description project")
    public SelenideElement editDescriptionProject(String name) {
        projectDescriptionInput.val("Description: " + name);
        saveEditProjectButton.click();
        return messageEdit.shouldBe(Condition.visible);
    }

    @Step("Edit project id project")
    public SelenideElement editProjectIdProject(String id) {
        projectIdInput.val(id + id);
        saveEditProjectButton.click();
        return messageEdit.shouldBe(Condition.visible);
    }

    @Step("Edit invalid project id project")
    public SelenideElement editUnsupportedCharacterProjectIdProject(String id) {
        projectIdInput.val(id + " " + id);
        saveEditProjectButton.click();
        return messageErrorEdit.shouldBe(Condition.visible);
    }

}
