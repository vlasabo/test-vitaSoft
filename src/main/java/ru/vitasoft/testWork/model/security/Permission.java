package ru.vitasoft.testWork.model.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Permission {
    ADMIN_PERMISSION("ADMIN"),
    USER_PERMISSION("USER"),
    OPERATOR_PERMISSION("OPERATOR");
    private final String permission;
}
