package com.example.teamcity.ui.pages;

import com.codeborne.selenide.*;
import com.example.teamcity.ui.elements.BuildProjectElement;
import io.qameta.allure.Step;
import lombok.Getter;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class BuildsPage extends BasePage{

    private static final String BUILDS_URL = "/project/%s";

    private ElementsCollection buildsProjectElements = $$("div[class*=BuildsByBuildType__container]");

    private SelenideElement header = $(".ProjectPageHeader__header--Z3");

    public SelenideElement runBuildButton = $("button[data-test='run-build']");
    @Step("Open build page")
    public static BuildsPage open(String projectName) {
        return Selenide.open(BUILDS_URL.formatted(projectName), BuildsPage.class);
    }

    public BuildsPage() {
        header.shouldBe(Condition.visible, BASE_WAITING);
    }

    public List<BuildProjectElement> getBuilds() {
        return generatePageElements(buildsProjectElements, BuildProjectElement::new);
    }

    public void clickRunBuildButton() {
        runBuildButton.click();
    }
}
