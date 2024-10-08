package com.example.teamcity.api;

import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.SourceProject;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static com.example.teamcity.api.enums.Endpoint.USERS;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static org.hamcrest.Matchers.equalTo;

@Test(groups = {"Regression"})
public class ProjectTest extends BaseApiTest{

    @Test(description = "User should be able to create project", groups = {"Positive", "CRUD"})
    public void userCreateProjectTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var project = userAuthSpec.getRequest(PROJECTS).create(testData.getProject());
        softy.assertEquals(testData.getProject(), project);
    }
    @Test(description = "User should be able to get details project", groups = {"Positive", "CRUD"})
    public void userGetProjectDetailsByIdTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());
        var project = userAuthSpec.getRequest(PROJECTS).read(testData.getProject().getId());

        softy.assertEquals(testData.getProject(), project);
    }

    @Test(description = "User should be able to get details project", groups = {"Positive", "CRUD"})
    public void userGetProjectDetailsByNameTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var project = userAuthSpec.<Project>getRequest(PROJECTS).create(testData.getProject());

        var response = userAuthSpec.<Project>getRequest(PROJECTS).read("name", project.getName());

        softy.assertEquals(response.as(Project.class).getName(), testData.getProject().getName());
    }

    @Test(description = "User should be able to archived project", groups = {"Positive", "CRUD"})
    public void userArchivedProjectTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());

        var projectArchived = userAuthSpec.getRequest(PROJECTS).update(testData.getProject().getId() + "/archived", null, "true");

        String responseBody = projectArchived.getBody().asString();
        softy.assertEquals(responseBody, "true", "Expected response body to be 'true'");
    }

    @Test(description = "User should be able to get data to status about archiving for project", groups = {"Positive", "CRUD"})
    public void userGeStatusArchivedProjectTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.<Project>getRequest(PROJECTS).create(testData.getProject());

        userAuthSpec.getRequest(PROJECTS).update(testData.getProject().getId() + "/archived", null, "true");

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .read(testData.getProject().getId())
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("archived", equalTo(true));
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

        softy.assertEquals(projectCopy.getId(), response.getId());
        softy.assertEquals(projectCopy.getName(), response.getName());
    }

    @Test(description = "User should be able to delete project by id", groups = {"Positive", "CRUD"})
    public void userDeleteProjectById() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .delete(testData.getProject().getId())
                .then()
                .assertThat().statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test(description = "User should be able to delete project by locator", groups = {"Positive", "CRUD"})
    public void userDeleteProjectByLocator() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new UncheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());

        var project = testData.getProject();
        project.setSourceProject(generate(SourceProject.class, project.getId()));

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .delete(testData.getProject().getSourceProject().getLocator())
                .then()
                .assertThat().statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test(description = "User cannot be able to create project same id project", groups = {"Negative", "CRUD"})
    public void userCannotCreateTwoProjectWithTheSameIdTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userAuthSpec.getRequest(PROJECTS).create(testData.getProject());

        testData.getProject().setId(testData.getProject().getId());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(testData.getProject())
                .then()
                .assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("DuplicateProjectNameException: Project with this name already exists: "
                        + testData.getProject().getName()
                        + "\nError occurred while processing this request."
                        .formatted(testData.getBuildType().getId())));
    }

}
