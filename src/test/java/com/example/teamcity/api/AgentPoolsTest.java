package com.example.teamcity.api;

import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.AGENTPOOLS;
import static com.example.teamcity.api.enums.Endpoint.USERS;

@Test(groups = {"Regression"})
public class AgentPoolsTest extends BaseApiTest {

    @Test(description = "User should be able get agent pools", groups = {"Positive", "CRUD"})
    public void userGetAgentPoolsTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        var response = uncheckedRequests.getRequest(AGENTPOOLS).read("")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(response.jsonPath().getString("agentPool[0].name"))
                .hasToString("Default");

    }
}
