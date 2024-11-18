package com.example.teamcity.api;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.agents.Agents;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.*;


public class AuthAgentTest extends BaseApiTest {
    @Test(groups = {"Setup"})
    public void authAgentTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        Response agents =  new UncheckedBase(Specifications.superUserSpec(), AGENTS_POOL)
                .read("name:Default")
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();

        var agentId = agents.jsonPath().getString("agents.agent[0].id");
        testData.getAuthorizedInfo().setStatus(true);

        Response response =  new UncheckedBase(Specifications.superUserSpec(), AGENTS)
                .update("id:" + agentId + "/authorizedInfo", testData.getAuthorizedInfo())
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertTrue(response.jsonPath().getBoolean("status"), "Status not true");
    }

    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read("id:" + testData.getBuildType().getId());
        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

}