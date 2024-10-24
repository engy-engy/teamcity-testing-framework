package com.example.teamcity.api;

import com.example.teamcity.api.models.*;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
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

    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read("id:" + testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should be able to run build type", groups = {"Positive", "CRUD"})
    public void userRunBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        testData.getBuildType().getSteps().getStep().get(0).getProperties().getProperty().get(0).setName("command.executable");
        testData.getBuildType().getSteps().getStep().get(0).getProperties().getProperty().get(0).setValue("/bin/bash");
        Property secondProperty = generate(Property.class);
        secondProperty.setName("command.parameters");
        secondProperty.setValue("-c echo Hello World!");
        List<Property> properties = new ArrayList<>(testData.getBuildType().getSteps().getStep().get(0).getProperties().getProperty());
        properties.add(secondProperty);
        testData.getBuildType().getSteps().getStep().get(0).getProperties().setProperty(properties);

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
                    .readByLocator("id", String.valueOf(buildId))
                    .then()
                    .assertThat().statusCode(HttpStatus.SC_OK)
                    .extract().response();

            List<Integer> buildIds = res.jsonPath().getList("build.id");

            return !buildIds.contains(buildId);
        });
    }

    @Test(description = "User should not be able to create to build types with the same id", groups = {"Negative","CRUD "})
    public void userCreatesTwoBuildTypeWithTheSameIdTest() {
        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        var response = new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then()
                .extract().response();

        softy.assertTrue(response.asString().contains("The build configuration / template ID \"%s\" is already used by another configuration or template\n"
                        .formatted(testData.getBuildType().getId())),
                "Expected error message not found in the response.");
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreateBuildTypeTest() {
        var createdUser = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userAuthSpec.<Project>getRequest(PROJECTS).create(testData.getProject());
        testData.getUser().setRoles(generate(Roles.class, PROJECT_ADMIN.getRoleName(), "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(USERS).update("id:" + createdUser.getId(), testData.getUser());

        var buildType = userAuthSpec.<BuildType>getRequest(BUILD_TYPES).create(testData.getBuildType());

        softy.assertEquals(testData.getBuildType().getName(), buildType.getName(), "Build type name is not correct");
    }

    @Test(description = "Project admin should not be able to create build type for not their project ", groups = {"Negative","Roles "})
    public void projectAdminCannotCreateBuildTypeForAnotherUserProjectTest(){
        var user1 = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var project = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        user1.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(USERS).update("id:" + user1.getId(), user1);

        userAuthSpec.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var user2 = superUserCheckRequests.<User>getRequest(USERS).create(generate(User.class));
        var project2 = superUserCheckRequests.<Project>getRequest(PROJECTS).create(generate(Project.class));

        user2.setRoles(generate(Roles.class, PROJECT_ADMIN.getRoleName(), "p:" + project.getId()));
        superUserCheckRequests.getRequest(USERS).update("id:" + user2.getId(), user2);

        var buildType2 = generate(BuildType.class);
        buildType2.getProject().setId(project2.getId());

        var response = new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildType2)
                .then()
                .extract().response();

        softy.assertTrue(response.asString().contains("You do not have enough permissions to edit project with id: "
                        + project2.getId() + "\n"
                        + "Access denied. Check the user has enough permissions to perform the operation."),
                "Expected error message not found in the response.");
    }

    @Test(description = "Project admin should not be able to create subproject with internal id _Root", groups = {"Negative","Roles "})
    public void projectAdminCannotCreateSubprojectWithoutPermissionTest(){
        var user1 = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        var projectId1 = testData.getProject().getId();
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());

        user1.setRoles(generate(Roles.class, PROJECT_ADMIN.getRoleName(), "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(USERS).update("id:" + user1.getId(), user1);

        var user2 = superUserCheckRequests.<User>getRequest(USERS).create(generate(User.class));
        var userAuthSpec2 = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        user2.setRoles(generate(Roles.class, PROJECT_ADMIN.getRoleName(), "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(USERS).update("id:" + user2.getId(), user2);


        generate(Project.class);
        var response1 = userAuthSpec.getRequest(PROJECTS)
                .create(testData.getProject())
                .then()
                .extract().response();

        softy.assertTrue(response1.asString().contains("You do not have \"Create subproject\" permission in project with internal id: _Root\n" +
                        "Access denied. Check the user has enough permissions to perform the operation."),
                "Expected error message not found in the response for user1.");

        var response2 = userAuthSpec2.getRequest(PROJECTS)
                .create(testData.getProject())
                .then()
                .extract().response();

        softy.assertTrue(response2.asString().contains("You do not have \"Create subproject\" permission in project with internal id: _Root\n" +
                        "Access denied. Check the user has enough permissions to perform the operation."),
                "Expected error message not found in the response for user2.");
        superUserCheckRequests.getRequest(PROJECTS).delete(projectId1);
    }
}
