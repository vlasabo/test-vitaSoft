package ru.vitasoft.testWork.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.vitasoft.testWork.model.user.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class RequestDtoIn {

    @NotBlank
    private String text;
    private LocalDateTime creationDate;
    private User user;

}
