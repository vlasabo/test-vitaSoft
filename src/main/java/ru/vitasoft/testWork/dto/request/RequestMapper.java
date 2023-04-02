package ru.vitasoft.testWork.dto.request;

import org.mapstruct.factory.Mappers;
import ru.vitasoft.testWork.dto.user.UserDtoMapper;
import ru.vitasoft.testWork.model.request.Request;
import ru.vitasoft.testWork.model.request.RequestStatus;

public class RequestMapper {
    private static final UserDtoMapper userDtoMapper = Mappers.getMapper(UserDtoMapper.class);

    public static Request toRequest(RequestDtoIn requestDtoIn) {
        return new Request(
                RequestStatus.valueOf(requestDtoIn.getStatus()),
                requestDtoIn.getText(),
                requestDtoIn.getCreationDate(),
                requestDtoIn.getUser()
        );
    }

    public static RequestDtoOut toRequestDtoOut(Request request) {
        return RequestDtoOut.builder()
                .status(request.getStatus())
                .text(request.getText())
                .creationDate(request.getCreationDate())
                .user(userDtoMapper.toUserDto(request.getUser()))
                .build();
    }
}
