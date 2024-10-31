package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildConfigurationPage extends CreateBasePage {

    private static final String BUILD_TYPE_MENU_SHOW_MODE = "createBuildTypeMenu";

    private SelenideElement buildConfigurationNameInput = $("#buildTypeName");
    private SelenideElement errorBuildTypeNameInput = $("#error_buildTypeName");


    public CreateBuildConfigurationPage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public static CreateBuildConfigurationPage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, BUILD_TYPE_MENU_SHOW_MODE), CreateBuildConfigurationPage.class);
    }

    public void setupBuildTypeConfiguration(String buildConfigurationName) {
        buildConfigurationNameInput.should(Condition.visible, BASE_WAITING);
        buildConfigurationNameInput.val(buildConfigurationName);
        proceedButton.click();
    }

    public SelenideElement setupSameNameBuildTypeConfiguration(String buildConfigurationName) {
        buildConfigurationNameInput.should(Condition.visible, BASE_WAITING);
        buildConfigurationNameInput.val(buildConfigurationName);
        proceedButton.click();
        return errorBuildTypeNameInput;
    }
}
