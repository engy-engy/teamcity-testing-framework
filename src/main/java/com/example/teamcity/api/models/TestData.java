package com.example.teamcity.api.models;

import lombok.Data;

@Data
public class TestData {

    private Project project;

    private User user;

    private BuildType buildType;

    private BuildQueue buildQueue;

    private AuthorizedInfo authorizedInfo;

    private NewProjectDescription newProjectDescription;

    private Build build;

}
