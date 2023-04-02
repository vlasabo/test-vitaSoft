package ru.vitasoft.testWork.model.user;

import lombok.Getter;

public enum Role {

    USER("Пользователь"),
    OPERATOR("Оператор"),
    ADMIN("Администратор");

    @Getter
    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

}
