package com.example.teamcity.api.enums;

import com.example.teamcity.api.models.*;
import com.example.teamcity.api.models.agent.AgentPool;
import com.example.teamcity.api.models.agent.AgentType;
import com.example.teamcity.api.models.agent.Agents;
import com.example.teamcity.api.models.audit.AuditEvents;
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
    ROLES("/app/rest/roles", Roles.class),
    BUILD_QUEUE("/app/rest/buildQueue", Properties.class),
    AGENTS("/app/rest/agents", Agents.class),
    AGENT_POOLS("/app/rest/agentPools", AgentPool.class),
    AGENT_TYPE("/app/rest/agentTypes", AgentType.class),
    AUDIT("/app/rest/audit", AuditEvents.class),
    SERVER("/app/rest/server", Server.class),;

    private final String url;
    private final Class<? extends BaseModel> modelClass;
}
