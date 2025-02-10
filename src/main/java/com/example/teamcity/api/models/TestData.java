package com.example.teamcity.api.models;

import com.example.teamcity.api.models.agent.Agent;
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

    private Agent agent;

}
