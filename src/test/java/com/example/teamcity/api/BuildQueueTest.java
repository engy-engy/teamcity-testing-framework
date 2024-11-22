package com.example.teamcity.api;

import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.models.Properties;
import com.example.teamcity.api.models.Property;
import com.example.teamcity.api.models.TestData;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.*;

@Test(groups = {"Regression"})
public class BuildQueueTest extends BaseApiTest{

    @Test(description = "User should be able add build to queue", groups = {"CRUD"})
    public void userAddBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        superUserCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        testData.getBuildType().getSteps().getStep().get(0).setProperties(new Properties());
        testData.getBuildType().getSteps().getStep().get(0).getProperties().setProperty(new ArrayList<>(Arrays.asList(
                new Property("script.content", "echo 'Hello World!'"),
                new Property("teamcity.step.mode", "default"),
                new Property("use.custom.script", "true"))));

        var response = uncheckedSuperUser.getRequest(BUILD_QUEUE).create(testData.getBuild())
                .then().statusCode(HttpStatus.SC_OK)
                .extract().response();

        softy.assertThat(response.jsonPath().getString("state")).isEqualTo("queued");
    }
}
