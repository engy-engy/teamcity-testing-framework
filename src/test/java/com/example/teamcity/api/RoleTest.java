package com.example.teamcity.api;

import com.example.teamcity.BaseTest;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.ROLES;
import static com.example.teamcity.api.enums.Endpoint.USERS;

@Test(groups = {"Regression"})
public class RoleTest extends BaseTest {


    @Test(description = "User should be able get all roles", groups = {"Positive", "CRUD"})
    public void userGetRoles() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequest = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var response = userCheckRequest.getRequest(ROLES).read("");
        softy.assertThat(response).isNotNull();
    }

    @Test(description = "User should be able get all roles by fields", groups = {"Positive", "CRUD"})
    public void userGetRolesByField() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequest = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var response = userCheckRequest.getRequest(ROLES).read("?fields=role(id,name,included(role(id,name)))");
        softy.assertThat(response).isNotNull();
    }

}
