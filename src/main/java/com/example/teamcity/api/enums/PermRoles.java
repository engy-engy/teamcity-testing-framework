package com.example.teamcity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление связывает каждый эндпоинт с соответствующей моделью данных.
 */
@AllArgsConstructor
@Getter
public enum PermRoles {
    PROJECT_ADMIN("PROJECT_ADMIN"),
    SYSTEM_ADMIN("SYSTEM_ADMIN"),
    PROJECT_VIEWER("PROJECT_VIEWER"),
    AGENT_MANAGER("AGENT_MANAGER"),
    TOOLS_INTEGRATION("TOOLS_INTEGRATION"),
    PROJECT_DEVELOPER("PROJECT_DEVELOPER");
    private final String roleName;

}
