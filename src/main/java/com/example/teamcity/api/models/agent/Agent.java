package com.example.teamcity.api.models.agent;

import com.example.teamcity.api.annotations.Random;
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
public class Agent extends BaseModel {

    @Random
    private int id;
    @Random
    private String name;

}
