package com.example.teamcity.api;

import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.*;


public class AuthAgentTest extends BaseApiTest {
    @Test(groups = {"Setup"})
    public void authAgentTest() {

        Response listAgentsPool =  new UncheckedBase(Specifications.superUserSpec(), AGENTS_POOL)
                .read("")
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();
        var agentPoolId = listAgentsPool.jsonPath().getString("agentPool[0].id");

        Response responseAgent =  new UncheckedBase(Specifications.superUserSpec(), AGENTS_POOL)
                .read("id:" + agentPoolId)
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();
        var agentId2 = responseAgent.jsonPath().getString("agents.agent[0].id");
        testData.getAuthorizedInfo().setStatus(true);

        Response response =  new UncheckedBase(Specifications.superUserSpec(), AGENTS)
                .update("id:" + agentId2 + "/authorizedInfo", testData.getAuthorizedInfo())
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertTrue(response.jsonPath().getBoolean("status"), "Status not true");

    }
}