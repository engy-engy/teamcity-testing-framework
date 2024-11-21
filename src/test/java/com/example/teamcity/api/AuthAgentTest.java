package com.example.teamcity.api;

import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.awaitility.core.ConditionTimeoutException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.example.teamcity.api.enums.Endpoint.AGENTS;
import static org.awaitility.Awaitility.await;

public class AuthAgentTest extends BaseApiTest {

    @Test(groups = {"Setup"})
    public void authAgentTest() {

        testData.getAuthorizedInfo().setStatus(true);
        String agentId = null;

        try {
            agentId = await().atMost(60, TimeUnit.SECONDS).until(() -> {
                Response agentResponse = new UncheckedBase(Specifications.superUserSpec(), AGENTS)
                        .read("?locator=authorized:any")
                        .then()
                        .assertThat().statusCode(HttpStatus.SC_OK)
                        .extract().response();

                String rawId = agentResponse.jsonPath().getString("agent[0].id");

                return rawId != null && !rawId.isEmpty() ? rawId : null;
            }, Objects::nonNull);
        } catch (ConditionTimeoutException e) {
            Assert.fail("Не удалось получить ID агента в течение 60 секунд", e);
        }

        Response response =  new UncheckedBase(Specifications.superUserSpec(), AGENTS)
                .update("id:" + agentId + "/authorizedInfo", testData.getAuthorizedInfo())
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertTrue(response.jsonPath().getBoolean("status"), "Status not true");
    }

}

