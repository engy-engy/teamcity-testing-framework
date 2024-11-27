package com.example.teamcity.api;

import com.example.teamcity.api.models.Agent;
import com.example.teamcity.api.models.AuthorizedInfo;
import com.example.teamcity.api.requests.checked.CheckedAgents;
import com.example.teamcity.api.spec.Specifications;
import io.qameta.allure.Step;
import org.awaitility.Awaitility;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.teamcity.api.generators.TestDataGenerator.generate;

public class AuthAgentTest extends BaseApiTest {

    @Test(groups = {"Setup"})
    public void setupTeamCityAgentTest() {
        var checkedAgentsRequest = new CheckedAgents(Specifications.superUserSpec());
        var agentId = waitUntilAgentIsFound(checkedAgentsRequest).getId();
        checkedAgentsRequest.update(agentId, generate(AuthorizedInfo.class));
    }

    @Step("Wait until agent is found")
    private Agent waitUntilAgentIsFound(CheckedAgents checkedAgentsRequest) {
        var atomicAgents = new AtomicReference<List<Agent>>();
        Awaitility.await()
                .until(() -> {
                    atomicAgents.set(checkedAgentsRequest.read("authorized:false").getAgent());
                    return !atomicAgents.get().isEmpty();
                });
        return atomicAgents.get().get(0);
    }
}

