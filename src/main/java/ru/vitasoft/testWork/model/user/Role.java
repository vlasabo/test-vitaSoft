package ru.vitasoft.testWork.model.user;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Role {

    USER("Пользователь"),
    OPERATOR("Оператор"),
    ADMIN("Администратор");

    private final String roleName;

}
