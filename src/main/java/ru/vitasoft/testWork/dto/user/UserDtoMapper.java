package ru.vitasoft.testWork.dto.user;

import org.mapstruct.Mapper;
import ru.vitasoft.testWork.model.user.User;

@Mapper
public interface UserDtoMapper {
    UserDto toUserDto(User user);
}
