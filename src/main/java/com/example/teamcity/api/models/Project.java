package com.example.teamcity.api.models;

import com.example.teamcity.api.annotations.Dependent;
import com.example.teamcity.api.annotations.Optional;
import com.example.teamcity.api.annotations.Parameterizable;
import com.example.teamcity.api.annotations.Random;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project extends BaseModel {

    @Dependent(relatedClass = NewProjectDescription.class)
    @Random
    private String id;

    @Dependent(relatedClass = NewProjectDescription.class)
    @Random
    private String name;

    private String locator;

    @Optional
    @Parameterizable
    private SourceProject sourceProject;


    @Parameterizable
    @Builder.Default
    private Boolean copyAllAssociatedSettings = Boolean.FALSE;

    @Parameterizable
    @Optional
    private String value;

}
