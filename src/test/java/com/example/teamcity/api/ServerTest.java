package com.example.teamcity.api;

import com.example.teamcity.api.models.Server;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.SERVER;
import static com.example.teamcity.api.enums.Endpoint.USERS;

@Test(groups = {"Regression"})
public class ServerTest extends BaseApiTest {
    @Test(description = "User should be able get server info", groups = {"Positive", "CRUD"})
    public void getServerInfoTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequest = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var response = userCheckRequest.<Server>getRequest(SERVER).read("");
        softy.assertThat(response).isNotNull();
    }
}
