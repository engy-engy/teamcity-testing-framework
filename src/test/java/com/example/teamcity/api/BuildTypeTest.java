package com.example.teamcity.api;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.checked.CheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest{

    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        var user = generate(User.class);

        var userRequester = new CheckedBase<User>(Specifications.superUserSpec(), Endpoint.USERS);
        userRequester.create(user);

        var project = generate(Project.class);

        var projectRequester = new CheckedBase<Project>(Specifications.authSpec(user), Endpoint.PROJECTS);
        project = projectRequester.create(project);

        var buildType = generate(Arrays.asList(project), BuildType.class);

        var buildTypeRequester = new CheckedBase<BuildType>(Specifications.authSpec(user), Endpoint.BUILD_TYPES);

        buildTypeRequester.create(buildType);

        var createdBuildType = buildTypeRequester.read(buildType.getId());
        softy.assertEquals(buildType.getName(), createdBuildType.getName(), "Build type name is not correct");

    }

    @Test(description = "User should not be able to create to build types with the same id", groups = {"Negative","CRUD "})
    public void userCreatesTwoBuildTypeWithTheSameIdTest(){

        step("Create user");
        step("Create project by user");
        step("Create buildType1 for project by user");
        step("Create buildType2 with same id as buildType1 for project by user");
        step("Check buildType2 was not created with bad request code");
    }

    @Test(description = "Project admin should be able to create build type for their project ", groups = {"Positive","Roles "})
    public void projectAdminCreateBuildTypeTest(){

        step("Create user");
        step("Create project");
        step("Grant user PROJECT_ADMIN role in project");

        step("Create buildType for project by user (PROJECT_ADMIN)");
        step("Check buildType was created successfully");
    }

    @Test(description = "Project admin should not be able to create build type for not their project ", groups = {"Negative","Roles "})
    public void projectAdminCreateBuildTypeForAnotherUserProjectTest(){

        step("Create user1");
        step("Create project1");
        step("Grant user PROJECT_ADMIN role in project1");

        step("Create user2");
        step("Create project2");
        step("Grant user PROJECT_ADMIN role in project2");

        step("Create buildType for project1 by user2");
        step("Check buildType was not created with forbidden code");
    }
}
