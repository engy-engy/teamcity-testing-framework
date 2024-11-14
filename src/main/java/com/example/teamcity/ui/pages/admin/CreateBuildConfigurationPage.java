package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;

public class CreateBuildConfigurationPage extends CreateBasePage {

    private static final String BUILD_TYPE_MENU_SHOW_MODE = "createBuildTypeMenu";

    private SelenideElement buildConfigurationNameInput = $("#buildTypeName");

    private SelenideElement errorBuildTypeNameInput = $("#error_buildTypeName");

    private SelenideElement progressLoader = $("#discoveryProgressContainer");

    public CreateBuildConfigurationPage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public static CreateBuildConfigurationPage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, BUILD_TYPE_MENU_SHOW_MODE), CreateBuildConfigurationPage.class);
    }

    public SelenideElement setupBuildTypeConfiguration(String buildConfigurationName, boolean waitForLoading) {
        buildConfigurationNameInput.should(Condition.visible, BASE_WAITING);
        buildConfigurationNameInput.val(buildConfigurationName);
        proceedButton.click();

        if (waitForLoading) {
            progressLoader.shouldBe(attribute("style", "display: none;"));
            return null; // Вернём null, чтобы показать, что ошибки нет
        }
        return errorBuildTypeNameInput;
    }
}
