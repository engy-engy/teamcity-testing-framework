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
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        step("Create project");
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        step("Grant user PROJECT_ADMIN role in project");
        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(USERS).update(createdUser.getId(), testData.getUser());

        step("Create buildType for project by user (PROJECT_ADMIN)");
        var buildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).create(testData.getBuildType());

        step("Check buildType was created successfully");
        softy.assertEquals(testData.getBuildType().getName(), buildType.getName(), "Build type name is not correct");

    }

    /*@Test(description = "Project admin should not be able to create build type for not their project ", groups = {"Negative","Roles "})
    public void projectAdminCannotCreateBuildTypeForAnotherUserProjectTest(){
        // no bugs вариант

        *//*
        step("Create project1 and project2 as superuser");
        var project1 = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var project2 = superUserCheckRequests.<Project>getRequest(PROJECTS).create(generate().getProject());
        var projectId1 = project1.getId();
        var projectId2 = project2.getId();
        System.out.println(projectId1);
        System.out.println(projectId2);
        step("Create user1 with PROJECT_ADMIN role in project1");
        var user1 = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        user1.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + project1.getId()));
        superUserCheckRequests.getRequest(USERS).update(user1.getId(), user1);


        step("Create user2 with PROJECT_ADMIN role in project1");
        var user2 = generate().getUser();
        user2 = superUserCheckRequests.<User>getRequest(USERS).create(user2);

        user2.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + project2.getId()));
        superUserCheckRequests.getRequest(USERS).update(user2.getId(), user2);


        step("Attempt to create build type for project1 by user2");
        user1.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + project2.getId()));
        superUserCheckRequests.getRequest(USERS).update(user1.getId(), user1);

        var buildType = generate().getBuildType();

        userCheckRequests.getRequest(BUILD_TYPES).create(buildType);
*//*
        // Мой вариант

        step("Create user1");
        var user1 = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));
        var userCheckRequests1 = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        superUserCheckRequests.getRequest(USERS).update(user1.getId(), user1);


        step("Create project1");
        userCheckRequests1.<Project>getRequest(PROJECTS).create(testData.getProject());
        var projectUser1 = testData.getProject();


        step("Create user2");
        var user2 = generate().getUser();
        superUserCheckRequests.<User>getRequest(USERS).create(user2);
        var userCheckRequests2 = new CheckedRequests(Specifications.authSpec(user2));

        step("Create project2");
        var project2 = generate().getProject();
        userCheckRequests2.<Project>getRequest(PROJECTS).create(project2);

        user1.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + project2.getId()));
        superUserCheckRequests.getRequest(USERS).update(user1.getId(), user1);

        //user2.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + project2.getId()));

        step("Create buildType for project1 by user2");
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(generate(BuildType.class))
                .then()
                .assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template\n"
                        .formatted(testData.getBuildType().getId())));
        userCheckRequests1.getRequest(BUILD_TYPES).create(testData.getBuildType());

        step("Check buildType was not created with forbidden code");
    }*/
}
