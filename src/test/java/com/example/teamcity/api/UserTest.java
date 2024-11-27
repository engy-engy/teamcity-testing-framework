package com.example.teamcity.api;

import com.example.teamcity.api.models.User;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.USERS;

@Test(groups = {"Regression"})
public class UserTest extends BaseApiTest {

    @Test(description = "The user should be able to get the name with the fields parameter", groups = {"Positive", "CRUD"})
    public void userGetNameWithFiledParameterTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var response = superUserCheckRequests.<User>getRequest(USERS).read("username:" + testData.getUser().getUsername() +"?fields=username");
        softy.assertThat(response.getUsername()).isEqualTo(testData.getUser().getUsername().toLowerCase());
    }
}
