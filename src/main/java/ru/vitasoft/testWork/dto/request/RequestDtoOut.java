package ru.vitasoft.testWork.dto.request;

import lombok.Builder;
import lombok.Data;
import ru.vitasoft.testWork.dto.user.UserDto;
import ru.vitasoft.testWork.model.request.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class RequestDtoOut {

    private RequestStatus status;
    private String text;
    private LocalDateTime creationDate;
    private UserDto user;

}
