package com.example.teamcity.api.models;

import com.example.teamcity.api.annotations.Optional;
import com.example.teamcity.api.annotations.Random;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewProjectDescription extends BaseModel {

    @Random
    private String id;

    @Random
    private String name;

    @Optional
    @Builder.Default
    private Project parentProject = new Project(
            null, null, "_Root",null,null,null);

}
