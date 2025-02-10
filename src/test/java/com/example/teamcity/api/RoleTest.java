package com.example.teamcity.api;

import com.example.teamcity.BaseTest;
import com.example.teamcity.api.models.Role;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.ROLES;
import static com.example.teamcity.api.enums.Endpoint.USERS;
import static com.example.teamcity.api.generators.RandomData.getString;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regression"})
public class RoleTest extends BaseTest {


    @Test(description = "User should be able get all roles", groups = {"Positive", "CRUD"})
    public void userGetRolesTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequest = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var response = userCheckRequest.<Roles>getRequest(ROLES).read("");
        softy.assertThat(response.getRole())
                .extracting(Role::getRoleId)
                .containsAnyOf("SYSTEM_ADMIN", "TOOLS_INTEGRATION", "AGENT_MANAGER", "PROJECT_ADMIN",
                        "PROJECT_DEVELOPER", "PROJECT_VIEWER");
    }

    @Test(description = "User should be able get all roles by fields", groups = {"Positive", "CRUD"})
    public void userGetRolesByFieldTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequest = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var response = userCheckRequest.<Roles>getRequest(ROLES).read("?fields=role(name)");

        softy.assertThat(response.getRole())
                .extracting(Role::getRoleId)
                .containsAnyOf("SYSTEM_ADMIN", "TOOLS_INTEGRATION", "AGENT_MANAGER", "PROJECT_ADMIN",
                        "PROJECT_DEVELOPER", "PROJECT_VIEWER");
    }

    @Test(description = "User should be able create role", groups = {"Positive", "CRUD"})
    public void userCreateRoleTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        var role = generate(Role.class);
        role.setRoleId(getString().toLowerCase());
        role.setScope(getString().toLowerCase());
        role.setName(getString().toLowerCase());

        var response = uncheckedRequests.getRequest(ROLES).create(role);

        softy.assertThat(response.jsonPath().getString("name")).isEqualTo(role.getName());
    }

    @Test(description = "User should be able create role by field", groups = {"Positive", "CRUD"})
    public void userCreateRoleByFieldTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var uncheckedRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        var role = generate(Role.class);
        role.setRoleId(getString().toLowerCase());
        role.setScope(getString().toLowerCase());
        role.setName(getString().toLowerCase());

        var response = uncheckedRequests.getRequest(ROLES).create("?fields=id,name", role);

        softy.assertThat(response.jsonPath().getString("name")).isEqualTo(role.getName());
    }

}
