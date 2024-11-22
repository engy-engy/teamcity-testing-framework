package com.example.teamcity.api;

import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.SourceProject;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static com.example.teamcity.api.enums.Endpoint.USERS;
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

}
