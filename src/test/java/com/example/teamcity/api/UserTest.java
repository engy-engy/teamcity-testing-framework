package com.example.teamcity.api;

import com.example.teamcity.api.models.User;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.USERS;

@Test(groups = {"Regression"})
public class UserTest extends BaseApiTest {

    @Test(description = "The user should be able to get the name with the fields parameter", groups = {"Positive", "CRUD"})
    public void userGetNameWithFieldParameterTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var response = superUserCheckRequests.<User>getRequest(USERS).read("username:" + testData.getUser().getUsername() +"?fields=username");
        softy.assertThat(response.getUsername()).isNotEmpty();
        softy.assertThat(response.getUsername()).isEqualTo(testData.getUser().getUsername().toLowerCase());
    }

    @Test(description = "The user should be able to get all users", groups = {"Positive", "CRUD"})
    public void userGetAllUserTest() {
        superUserCheckRequests.getRequest(USERS).create("?fields=name", testData.getUser());

        var response = uncheckedSuperUser.getRequest(USERS).read("?fields=username")
                .then().statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(response).isNotNull();
    }

    @Test(description = "User should be able create user with the fields parameter", groups = {"Positive", "CRUD"})
    public void userCreatesUserWithFieldParameterTest() {
        var response = uncheckedSuperUser.getRequest(USERS)
                .create("?fields=username", testData.getUser())
                .then().statusCode(HttpStatus.SC_OK)
                .extract().response();

        softy.assertThat(response.jsonPath().getString("username")).isEqualTo(testData.getUser().getUsername().toLowerCase());
    }

}
