package com.example.teamcity.api;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {

    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should not be able to create to build types with the same id", groups = {"Negative","CRUD "})
    public void userCreatesTwoBuildTypeWithTheSameIdTest() {
        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then()
                .assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template\n"
                        .formatted(testData.getBuildType().getId())));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreateBuildTypeTest() {

        step("Create user");
        var createdUser = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        step("Create project");
        userAuthSpec.<Project>getRequest(PROJECTS).create(testData.getProject());

        step("Grant user PROJECT_ADMIN role in project");
        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(USERS).update(createdUser.getId(), testData.getUser());

        step("Create buildType for project by user (PROJECT_ADMIN)");
        var buildType = userAuthSpec.<BuildType>getRequest(BUILD_TYPES).create(testData.getBuildType());

        step("Check buildType was created successfully");
        softy.assertEquals(testData.getBuildType().getName(), buildType.getName(), "Build type name is not correct");

    }

    @Test(description = "Project admin should not be able to create build type for not their project ", groups = {"Negative","Roles "})
    public void projectAdminCannotCreateBuildTypeForAnotherUserProjectTest(){
        var user1 = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var project = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        user1.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(USERS).update(user1.getId(), user1);

        userAuthSpec.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var user2 = superUserCheckRequests.<User>getRequest(USERS).create(generate(User.class));
        var project2 = superUserCheckRequests.<Project>getRequest(PROJECTS).create(generate(Project.class));
        var userAuthSpec2 = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        user2.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + project.getId()));
        superUserCheckRequests.getRequest(USERS).update(user2.getId(), user2);

        var buildType2 = generate(BuildType.class);
        buildType2.getProject().setId(project2.getId());
        userAuthSpec2.getRequest(BUILD_TYPES)
                .create(buildType2)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString("You do not have enough permissions to edit project with id: "
                        + project2.getId() + "\n"
                        + "Access denied. Check the user has enough permissions to perform the operation."
                        .formatted(testData.getBuildType().getId())));
    }

    @Test(description = "Project admin should not be able to create subproject with internal id _Root", groups = {"Negative","Roles "})
    public void projectAdminCannotCreateSubprojectWithoutPermissionTest(){
        var user1 = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        var project = testData.getProject();
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());

        user1.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(USERS).update(user1.getId(), user1);

        var user2 = superUserCheckRequests.<User>getRequest(USERS).create(generate(User.class));
        var userAuthSpec2 = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        user2.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(USERS).update(user2.getId(), user2);

        generate(Project.class);

        userAuthSpec.getRequest(PROJECTS)
                .create(project)
                .then()
                .assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString("You do not have \"Create subproject\" permission in project with internal id: _Root\n"
                        + "Access denied. Check the user has enough permissions to perform the operation."
                        .formatted(testData.getBuildType().getId())));

        userAuthSpec2.getRequest(PROJECTS)
                .create(project)
                .then()
                .assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString("You do not have \"Create subproject\" permission in project with internal id: _Root\n"
                        + "Access denied. Check the user has enough permissions to perform the operation."
                        .formatted(testData.getBuildType().getId())));
    }
}
