package com.example.teamcity.api.models;

import com.example.teamcity.api.annotations.Optional;
import com.example.teamcity.api.annotations.Parameterizable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildQueue extends BaseModel {
    private BuildTypeId buildType;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BuildTypeId extends BaseModel{
        @Parameterizable
        @Optional
        private String id;
    }
}
