package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.example.teamcity.api.models.Properties;
import com.example.teamcity.api.models.Property;
import com.example.teamcity.ui.pages.BuildConfigurationPage;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class RunBuildTypeTest extends BaseUiTest {

    @Test(description = "User should be able to run build type with command line `Hello world!` ", groups = {"Positive"})
    public void userRunBuildTypeTest() {

        step("Login as user");
        loginAs(testData.getUser());

        step("Create project -> API");
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        step("Create build type -> API");
        testData.getBuildType().getSteps().getStep().get(0).setProperties(new Properties());
        testData.getBuildType().getSteps().getStep().get(0).getProperties().setProperty(new ArrayList<>(Arrays.asList(
                new Property("script.content", "echo 'Hello, World!'"),
                new Property("teamcity.step.mode", "default"),
                new Property("use.custom.script", "true"))));
        superUserCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        step("Open Build Configuration Page and Run Build Type Configuration");
        ElementsCollection logResult = BuildConfigurationPage.open(testData.getBuildType().getId())
                .clickRunBuildButton()
                .clickBuildElement("1")
                .clickBuildLogButton()
                .findValueInLog("Hello, World!");

        step("Check that text `Hello, world!` exist in build logs");
        logResult.filter(Condition.text("Hello, World!")).forEach(element -> {
            softy.assertTrue(element.getText().contains("Hello, World!"), "Текст 'Hello, World!' не найден в элементе");
        });
    }
}
