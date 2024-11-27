package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class BuildConfigurationPage extends BasePage{

    private static final String BUILDS_URL = "/buildConfiguration/%s";

    private ElementsCollection buildsElements = $$(".Grid__grid--k5");

    public SelenideElement runBuildButton = $("button[data-test='run-build']");
    public SelenideElement buildLogButton = $(".ring-tabs-container:nth-of-type(1) .ring-tabs-visible span[data-tab-title='Build Log']");
    public SelenideElement searchLogButton = $("button[data-test-full-build-log='open-search']");
    public SelenideElement inputSearchLog = $("input[aria-label^='Search']");
    public SelenideElement searchValueButton = $(".ring-button-toolbar-buttonToolbar");
    public ElementsCollection logResult = $$(".LogMessage__text--ly");

    @Step("Open build configuration page")
    public static BuildConfigurationPage open(String projectName) {
        return Selenide.open(BUILDS_URL.formatted(projectName), BuildConfigurationPage.class);
    }

    public BuildConfigurationPage() {
     // to do
    }

    public BuildConfigurationPage clickRunBuildButton() {
        runBuildButton.click();
        return this;
    }

    public BuildConfigurationPage clickBuildLogButton() {
        buildLogButton.shouldBe(Condition.visible).click();
        return this;
    }

    public SelenideElement generateLocatorBuildNumber(String buildNumber) {
        return $("span[aria-label*='#" + buildNumber + "']");
    }

    public BuildConfigurationPage clickBuildElement(String buildNumber) {
        generateLocatorBuildNumber(buildNumber).shouldBe(Condition.visible).click();
        return this;
    }

    public ElementsCollection findValueInLog(String value) {
        buildLogButton.shouldBe(Condition.visible).click();
        searchLogButton.shouldBe(Condition.visible).click();
        inputSearchLog.shouldBe(Condition.visible).val(value);
        searchValueButton.shouldBe(Condition.visible).click();

        return logResult;
    }
}
