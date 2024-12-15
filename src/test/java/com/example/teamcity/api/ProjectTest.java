package com.example.teamcity.api;

import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.*;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.enums.PermRoles.PROJECT_ADMIN;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regression"})
public class ProjectTest extends BaseApiTest{

    @Test(description = "User should be able to create project", groups = {"Positive", "CRUD"})
    public void userCreateProjectTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var project = userAuthSpec.getRequest(PROJECTS).create(testData.getProject());
        softy.assertThat(project).isEqualTo(testData.getProject());
    }

    @Test(description = "User should be able to get details project", groups = {"Positive", "CRUD"})
    public void userGetProjectDetailsByIdTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());
        var project = userAuthSpec.getRequest(PROJECTS).read("id:" + testData.getProject().getId());
        softy.assertThat(testData.getProject()).isEqualTo(project);
    }

    @Test(description = "User should be able to get details project", groups = {"Positive", "CRUD"})
    public void userGetProjectDetailsByNameTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var project = userAuthSpec.<Project>getRequest(PROJECTS).create(testData.getProject());
        var response = userAuthSpec.<Project>getRequest(PROJECTS).read("name:" + project.getName());
        softy.assertThat(response.getName()).isEqualTo(testData.getProject().getName());
    }

    @Test(description = "User should be able to get project by fields parameter", groups = {"Positive", "CRUD"})
    public void userGetProjectByFieldParameterTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.<Project>getRequest(PROJECTS).create(testData.getProject());
        var response = userAuthSpec.<Project>getRequest(PROJECTS).read("name:" + testData.getProject().getName() + "?field=name");
        softy.assertThat(response.getName()).isEqualTo(testData.getProject().getName());
    }

    @Test(description = "User should be able to archived project", groups = {"Positive", "CRUD"})
    public void userArchivedProjectTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());

        var projectArchived = RestAssured
                .given()
                .spec(Specifications.authSpec(testData.getUser()))
                .accept("text/plain")
                .contentType("text/plain")
                .body("true")
                .put(PROJECTS.getUrl() + "/id:" + testData.getProject().getId() + "/archived")
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();

        String responseBody = projectArchived.getBody().asString();
        softy.assertThat(responseBody).isEqualTo("true");
    }

    @Test(description = "User should be able to get data to status about archiving for project", groups = {"Positive", "CRUD"})
    public void userGeStatusArchivedProjectTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());

        RestAssured
                .given()
                .spec(Specifications.authSpec(testData.getUser()))
                .accept("text/plain")
                .contentType("text/plain")
                .body("true")
                .put(PROJECTS.getUrl() + "/id:" + testData.getProject().getId() + "/archived")
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();

        Response response =  new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .read(testData.getProject().getId())
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();

        boolean currentName = Boolean.parseBoolean(response.jsonPath().getString("archived"));
        softy.assertThat(currentName).isEqualTo(true);
    }

    @Test(description = "User should be able to copy project", groups = {"Positive", "CRUD"})
    public void userCopyProjectTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var project = userAuthSpec.<Project>getRequest(PROJECTS).create(testData.getProject());

        var projectCopy = generate(Project.class);
        projectCopy.setSourceProject(generate(SourceProject.class, project.getId()));
        projectCopy.setCopyAllAssociatedSettings(Boolean.TRUE);

        var response = userAuthSpec.<Project>getRequest(PROJECTS).create(projectCopy);

        softy.assertThat(projectCopy.getId()).isEqualTo(response.getId());
        softy.assertThat(projectCopy.getName()).isEqualTo(response.getName());

    }

    @Test(description = "User should be able to delete project by id", groups = {"Positive", "CRUD"})
    public void userDeleteProjectById() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .delete("id:" + testData.getProject().getId())
                .then()
                .assertThat().statusCode(HttpStatus.SC_NO_CONTENT);

        var responseCode = new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .read("id:" + testData.getProject().getId())
                .then()
                .extract().response().statusCode();

        softy.assertThat(responseCode).isEqualTo(HttpStatus.SC_NOT_FOUND);
    }

    @Test(description = "User should be able to delete project by locator", groups = {"Positive", "CRUD"})
    public void userDeleteProjectByLocator() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());

        var project = testData.getProject();
        project.setSourceProject(generate(SourceProject.class, project.getId()));

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .delete("id:" + testData.getProject().getSourceProject().getLocator())
                .then()
                .assertThat().statusCode(HttpStatus.SC_NO_CONTENT);

        var responseCode = new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .read("id:" + testData.getProject().getId())
                .then()
                .extract().response().statusCode();

        softy.assertThat(responseCode).isEqualTo(HttpStatus.SC_NOT_FOUND);
    }

    @Test(description = "User cannot be able to create project same id project", groups = {"Negative", "CRUD"})
    public void userCannotCreateTwoProjectWithTheSameIdTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());

        testData.getProject().setId(testData.getProject().getId());

        var response = new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(testData.getProject())
                .then()
                .extract().response();

        softy.assertThat(response.asString())
                .contains("DuplicateProjectNameException: Project with this name already exists: "
                        + testData.getProject().getName());

    }

    @Test(description = "Project admin should not be able to create subproject with internal id _Root", groups = {"Negative","Roles "})
    public void projectAdminCannotCreateSubprojectWithoutPermissionTest(){
        var user1 = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        var project = testData.getProject();
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

        softy.assertThat(response1.asString())
                .contains("You do not have \"Create subproject\" permission in project with internal id: _Root")
                .contains("Access denied. Check the user has enough permissions to perform the operation.");

        var response2 = userAuthSpec2.getRequest(PROJECTS)
                .create(testData.getProject())
                .then()
                .extract().response();

        softy.assertThat(response2.asString())
                .contains("You do not have \"Create subproject\" permission in project with internal id: _Root")
                .contains("Access denied. Check the user has enough permissions to perform the operation.");
        TestDataStorage.getStorage().addCreatedEntity(PROJECTS, project);
    }

    @Test(description = "Project admin should not be able to create build type for not their project ", groups = {"Negative","Roles "})
    public void projectAdminCannotCreateBuildTypeForAnotherUserProjectTest(){
        var user1 = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var project = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        user1.setRoles(generate(Roles.class, PROJECT_ADMIN.getRoleName(), "p:" + testData.getProject().getId()));
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

        softy.assertThat(response.asString())
                .contains("You do not have enough permissions to edit project with id: " + project2.getId())
                .contains("Access denied. Check the user has enough permissions to perform the operation.");

    }

    @Test(description = "User should be able to create project bad request", groups = {"Negative", "CRUD"})
    public void userCreateProjectBadRequestTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());
        var response = uncheckedSuperUser.getRequest(PROJECTS).create(testData.getProject())
                .then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();
        softy.assertThat(response.asString())
                .contains("Project with this name already exists: "
                        + testData.getProject().getName());
    }

    @Test(description = "User should be able not create project with empty name", groups = {"Negative", "CRUD"})
    public void userCreateProjectWithEmptyNameTest() {
        testData.getProject().setName("");
        var response = uncheckedSuperUser.getRequest(PROJECTS)
                .create(testData.getProject())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        softy.assertThat(response.asString())
                .contains("BadRequestException: Project name cannot be empty."
                        + testData.getProject().getName());

    }

}
