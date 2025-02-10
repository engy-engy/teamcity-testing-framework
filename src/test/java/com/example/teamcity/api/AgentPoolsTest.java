package com.example.teamcity.api;

import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.*;

@Test(groups = {"Regression"})
public class AgentPoolsTest extends BaseApiTest {

    @Test(description = "User should be able get all agent pools", groups = {"Positive", "CRUD"})
    public void userGetAllAgentPoolsTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        var response = uncheckedRequests.getRequest(AGENT_POOLS).read("")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(response.jsonPath().getString("agentPool[0].name"))
                .hasToString("Default");

    }

    @Test(description = "User should be able create agent", groups = {"Positive", "CRUD"})
    public void userCreateAgentPoolsTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        var response = uncheckedRequests.getRequest(AGENT_POOLS).create(testData.getAgent())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(response.jsonPath().getString("name"))
                .hasToString(testData.getAgent().getName());
    }

    @Test(description = "User should be able delete agent", groups = {"Positive", "CRUD"})
    public void userDeleteAgentPoolsTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        var agent = uncheckedRequests.getRequest(AGENT_POOLS).create(testData.getAgent());

        uncheckedRequests.getRequest(AGENT_POOLS).delete("name:" + agent.jsonPath().getString("name"))
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
        uncheckedRequests.getRequest(AGENT_POOLS).delete("name:" + agent.jsonPath().getString("name"))
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test(description = "User should be able get agent pool by locator", groups = {"Positive", "CRUD"})
    public void userGetAgentPoolsByLocatorTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        var agent = uncheckedRequests.getRequest(AGENT_POOLS).create(testData.getAgent());

        uncheckedRequests.getRequest(AGENT_POOLS).read("name:" + agent.jsonPath().getString("name"))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(agent.jsonPath().getString("name"))
                .hasToString(testData.getAgent().getName());
    }

    @Test(description = "User should be able get agent pool by locator with field", groups = {"Positive", "CRUD"})
    public void userGetAgentPoolsByLocatorWithFieldTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        var agent = uncheckedRequests.getRequest(AGENT_POOLS).create("?fields=name", testData.getAgent());

        uncheckedRequests.getRequest(AGENT_POOLS).read("name:" + agent.jsonPath().getString("name") + "?fields=name")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(agent.jsonPath().getString("name"))
                .hasToString(testData.getAgent().getName());
    }

    @Test(description = "User should be able get agent from agent pools with field", groups = {"Positive", "CRUD"})
    public void userGetAgentFromAgentPoolWithFieldTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        var agent = uncheckedRequests.getRequest(AGENT_POOLS).create(testData.getAgent());

        uncheckedRequests.getRequest(AGENT_POOLS).read(
                "name:" + agent.jsonPath().getString("name") + "/agents" + "?fields=name")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(agent.jsonPath().getString("name"))
                .hasToString(testData.getAgent().getName());
    }

    @Test(description = "User should be able set agent pool for project", groups = {"Positive", "CRUD"})
    public void userSetAgentForProjectTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        uncheckedRequests.getRequest(PROJECTS).create(testData.getProject());
        var agent = uncheckedRequests.getRequest(AGENT_POOLS).create(testData.getAgent());

        uncheckedRequests.getRequest(AGENT_POOLS).update(
                "name:" + agent.jsonPath().getString("name") + "/projects", testData.getProject())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(agent.jsonPath().getString("name"))
                .hasToString(testData.getAgent().getName());
        superUserCheckRequests.getRequest(PROJECTS).delete(testData.getProject().getId());
    }

    @Test(description = "User should be able delete agent pool for project", groups = {"Positive", "CRUD"})
    public void userDeleteAgentForProjectTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        uncheckedRequests.getRequest(PROJECTS).create(testData.getProject());
        var agent = uncheckedRequests.getRequest(AGENT_POOLS).create(testData.getAgent());

        uncheckedRequests.getRequest(AGENT_POOLS).delete(
                "name:" + agent.jsonPath().getString("name") + "/projects" + "/name:" + testData.getProject().getName())
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .extract().response();
        softy.assertThat(agent.jsonPath().getString("name"))
                .hasToString(testData.getAgent().getName());
        superUserCheckRequests.getRequest(PROJECTS).delete(testData.getProject().getId());
    }

}
