package com.example.teamcity.api.models;

import com.example.teamcity.api.models.agents.AuthorizedInfo;
import lombok.Data;

@Data
public class TestData {
    private Project project;
    private User user;
    private BuildType buildType;
    private SourceProject sourceProject;
    private BuildQueue buildQueue;
    private AuthorizedInfo authorizedInfo;
}
