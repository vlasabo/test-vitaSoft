package ru.vitasoft.testWork.dto.user;

import ru.vitasoft.testWork.model.user.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getUsername()
        );
    }
}
