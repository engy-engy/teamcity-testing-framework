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

        Response listAgentsPool =  new UncheckedBase(Specifications.superUserSpec(), AGENTS)
                .read("?locator=authorized:any")
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();
        var agentId = listAgentsPool.jsonPath().getString("agent[0].id");

        testData.getAuthorizedInfo().setStatus(true);

        Response response =  new UncheckedBase(Specifications.superUserSpec(), AGENTS)
                .update("id:" + agentId + "/authorizedInfo", testData.getAuthorizedInfo())
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertTrue(response.jsonPath().getBoolean("status"), "Status not true");
    }
}