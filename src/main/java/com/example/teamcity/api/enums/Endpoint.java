package com.example.teamcity.api.enums;

import com.example.teamcity.api.models.*;
import com.example.teamcity.api.models.Agents;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление связывает каждый эндпоинт с соответствующей моделью данных.
 */
@AllArgsConstructor
@Getter
public enum Endpoint {
    BUILD_TYPES("/app/rest/buildTypes", BuildType.class),
    PROJECTS("/app/rest/projects", Project.class),
    USERS("/app/rest/users", User.class),
    ROLES("/app/rest/users", Roles.class),
    BUILD_QUEUE("/app/rest/buildQueue", Properties.class),
    AGENTS("/app/rest/agents", Agents.class);

    private final String url;
    private final Class<? extends BaseModel> modelClass;
}
