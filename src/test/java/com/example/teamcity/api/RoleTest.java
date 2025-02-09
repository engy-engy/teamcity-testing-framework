package com.example.teamcity.api;

import com.example.teamcity.BaseTest;
import com.example.teamcity.api.generators.RandomData;
import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Role;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.RandomData.getString;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

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

    @Test(description = "User should be able create role", groups = {"Positive", "CRUD"})
    public void userCreateRole() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        var role = generate(Role.class);
        role.setRoleId(getString().toLowerCase());
        role.setScope(getString().toLowerCase());
        role.setName(getString().toLowerCase());

        var response = uncheckedRequests.getRequest(ROLES).create(role);

        softy.assertThat(response.jsonPath().getString("name")).isEqualTo(role.getName());
    }

}
