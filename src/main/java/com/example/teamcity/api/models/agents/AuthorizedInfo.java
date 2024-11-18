package com.example.teamcity.api.models.agents;

import com.example.teamcity.api.annotations.Parameterizable;
import com.example.teamcity.api.models.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizedInfo extends BaseModel {
    @Parameterizable
    private boolean status;
}
