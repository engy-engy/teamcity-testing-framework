package com.example.teamcity.api;

import com.example.teamcity.api.generators.RandomData;
import com.example.teamcity.api.models.*;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.checked.CheckedBase;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.enums.PermRoles.PROJECT_ADMIN;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {

    private static final int BUILD_TYPE_ID_CHARACTERS_LIMIT = 225;
    private CheckedBase<BuildType> checkedBuildTypeRequest;
    private UncheckedBase uncheckedBuildTypeRequest;

    @BeforeMethod(alwaysRun = true)
    public void getRequest() {
        checkedBuildTypeRequest = new CheckedBase<>(Specifications.authSpec(testData.getUser()), BUILD_TYPES);
        uncheckedBuildTypeRequest = new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES);
    }


    @Test(description = "User should be able to create build type with id", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeWithIdTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var response = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read("id:" + testData.getBuildType().getId() + "?fields=name");
        softy.assertThat(response.getName()).as("buildName").isEqualTo(testData.getBuildType().getName());
    }

    @Test(description = "User should be able get all build types", groups = {"Positive", "CRUD"})
    public void userGetAllBuildTypes() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        userCheckRequests.getRequest(BUILD_TYPES).create("fields=name", testData.getBuildType());

        var response = userCheckRequests.getRequest(BUILD_TYPES)
                .read("?name:"+testData.getBuildType().getName()+"&fields=name")
                .then().extract().response();
        softy.assertThat(response).isNotNull();
    }

    @Test(description = "User should be able to create build type with field parameter", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeWithFieldParameterTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userUnCheckRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        userUnCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        var response = userUnCheckRequests.getRequest(BUILD_TYPES).create("?fields=name", testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();
        softy.assertThat(response.jsonPath().getString("name")).isEqualTo(testData.getBuildType().getName());
    }

    @Test(description = "User should be able get build type with fields parameter", groups = {"Positive", "CRUD"})
    public void userGetBuildTypeWithFieldParameterTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var response = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read("name:" + testData.getBuildType().getName() +"?fields=name");
        softy.assertThat(response.getName()).isEqualTo(testData.getBuildType().getName());
    }

    @Test(description = "User should be able to delete build type with locator", groups = {"CRUD"})
    public void userCreatesBuildTypeWithLocatorTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        checkedBuildTypeRequest.create(testData.getBuildType());
        var response = checkedBuildTypeRequest.read("name:" + testData.getBuildType().getName());

        softy.assertThat(response.getId()).as("buildTypeId").isEqualTo(testData.getBuildType().getId());
    }

    @Test(description = "User should be able to run build type", groups = {"Positive", "CRUD"})
    public void userRunBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        testData.getBuildType().getSteps().getStep().get(0).setProperties(new Properties());
        testData.getBuildType().getSteps().getStep().get(0).getProperties().setProperty(new ArrayList<>(Arrays.asList(
                new Property("script.content", "echo 'Hello World!'"),
                new Property("teamcity.step.mode", "default"),
                new Property("use.custom.script", "true"))));

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read("id:" + testData.getBuildType().getId());

        generate(BuildQueue.class);
        testData.getBuildQueue().getBuildType().setId(testData.getBuildType().getId());

        Response response = new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_QUEUE)
                .create(testData.getBuildQueue())
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .body("state", equalTo("queued"))
                .extract().response();

        int buildId = response.jsonPath().getInt("id");

        await().atMost(60, TimeUnit.SECONDS).until(() -> {
            Response res = new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_QUEUE)
                    .search("id", String.valueOf(buildId))
                    .then()
                    .assertThat().statusCode(HttpStatus.SC_OK)
                    .extract().response();

            List<Integer> buildIds = res.jsonPath().getList("build.id");

            return !buildIds.contains(buildId);
        });
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"CRUD"})
    public void userCreatesTwoBuildTypesWithSameIdTest() {
        uncheckedSuperUser.getRequest(USERS).create(testData.getUser());
        uncheckedSuperUser.getRequest(PROJECTS).create(testData.getProject());

        checkedBuildTypeRequest.create(testData.getBuildType());

        var secondTestData = generate();

        var secondBuildTypeTestData = secondTestData.getBuildType();
        secondBuildTypeTestData.setId(testData.getBuildType().getId());
        secondBuildTypeTestData.setProject(testData.getBuildType().getProject());

        uncheckedBuildTypeRequest.create(secondBuildTypeTestData)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract();

        softy.assertThat(secondBuildTypeTestData.getId()).isEqualTo(testData.getBuildType().getId());
    }

    @Test(description = "User should not be able to create build type with id exceeding the limit", groups = {"CRUD"})
    public void userCreatesBuildTypeWithIdExceedingLimitTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        testData.getBuildType().setId(RandomData.getString(BUILD_TYPE_ID_CHARACTERS_LIMIT + 1));

        uncheckedBuildTypeRequest.create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        testData.getBuildType().setId(RandomData.getString(BUILD_TYPE_ID_CHARACTERS_LIMIT));

        checkedBuildTypeRequest.create(testData.getBuildType());

        //to do softy
    }

    @Test(description = "Unauthorized user should not be able to create build type", groups = {"CRUD"})
    public void unauthorizedUserCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        uncheckedBuildTypeRequest.create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);

        var response = uncheckedSuperUser.getRequest(BUILD_TYPES).read(testData.getBuildType().getId())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                        .extract().asString();

        softy.assertThat(response).contains("No build type or template is found by id, internal id or name '" + testData.getBuildType().getId() + "'");
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreateBuildTypeTest() {

        var createdUser = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userAuthSpec.<Project>getRequest(PROJECTS).create(testData.getProject());
        testData.getUser().setRoles(generate(Roles.class, PROJECT_ADMIN.getRoleName(), "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(USERS).update("id:" + createdUser.getId(), testData.getUser());

        var response = userAuthSpec.<BuildType>getRequest(BUILD_TYPES).create(testData.getBuildType());

        softy.assertThat(response.getName()).isEqualTo(testData.getBuildType().getName());
    }

    @Test(description = "User should be able to delete build type", groups = {"Regression"})
    public void userDeletesBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        checkedBuildTypeRequest.create(testData.getBuildType());
        checkedBuildTypeRequest.delete(testData.getBuildType().getId());

        var response = uncheckedBuildTypeRequest.read(testData.getBuildType().getId())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .extract();
        softy.assertThat(response.asString())
                .contains("No build type or template is found by id, internal id or name '" + testData.getBuildType().getId() + "'");
    }

}
