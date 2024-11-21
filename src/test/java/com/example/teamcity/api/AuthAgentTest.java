package com.example.teamcity.api;

import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.example.teamcity.api.enums.Endpoint.*;
import static org.awaitility.Awaitility.await;

public class AuthAgentTest extends BaseApiTest {
    @Test(groups = {"Setup"})
    public void authAgentTest() {
        testData.getAuthorizedInfo().setStatus(true);

        await().atMost(60, TimeUnit.SECONDS).until(() -> {
            Response checkAgentsResponse = new UncheckedBase(Specifications.superUserSpec(), AGENTS)
                    .read("?locator=authorized:any")
                    .then()
                    .assertThat().statusCode(HttpStatus.SC_OK)
                    .extract().response();

            String currentAgentId = checkAgentsResponse.jsonPath().getString("agent[0].id");
            return currentAgentId != null && !currentAgentId.isEmpty();
        });

        Response listAgentsPool = new UncheckedBase(Specifications.superUserSpec(), AGENTS)
                .read("?locator=authorized:any")
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();
        var agentId = listAgentsPool.jsonPath().getString("agent[0].id");

        Response response =  new UncheckedBase(Specifications.superUserSpec(), AGENTS)
                .update("id:" + agentId + "/authorizedInfo", testData.getAuthorizedInfo())
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertTrue(response.jsonPath().getBoolean("status"), "Status not true");
    }
}