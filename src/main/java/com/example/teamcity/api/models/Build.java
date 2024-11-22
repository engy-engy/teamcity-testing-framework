package com.example.teamcity.api.models;

import com.example.teamcity.api.annotations.Optional;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Чтобы избежать ворнинга "Generating equals/hashCode implementation but without a call to superclass."
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class Build extends BaseModel {

    private BuildType buildType;

    @Optional
    private String state;

}
