package com.example.teamcity.api;

import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.AGENT_TYPE;
import static com.example.teamcity.api.enums.Endpoint.USERS;

@Test(groups = {"Regression"})
public class AgentTypeTest extends BaseApiTest{

    @Test(description = "User should be able get agent type", groups = {"Positive", "CRUD"})
    public void userGetAgentTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        var response = uncheckedRequests.getRequest(AGENT_TYPE).read("?agentTypeLocator:" + testData.getAgent().getName())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(response).isNotNull();
    }

    @Test(description = "User should be able get agent type with field", groups = {"Positive", "CRUD"})
    public void userGetAgentTypeWithFieldTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        var response = uncheckedRequests.getRequest(AGENT_TYPE).read("?agentTypeLocator:" + testData.getAgent().getName() + "&fields=count")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(response.jsonPath().getInt("count")).isNotNull();
    }
}
