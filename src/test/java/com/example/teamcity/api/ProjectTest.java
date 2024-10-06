package com.example.teamcity.api;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regression"})
public class ProjectTest extends BaseApiTest{

    @Test(description = "User should be able to create project", groups = {"Positive", "CRUD"})
    public void userSuccessCreateProjectTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userAuthSpec = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var project = userAuthSpec.getRequest(PROJECTS).create(testData.getProject());
        softy.assertEquals(testData.getProject(), project);
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
