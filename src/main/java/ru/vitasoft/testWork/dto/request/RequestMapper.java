package ru.vitasoft.testWork.dto.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vitasoft.testWork.dto.user.UserMapper;
import ru.vitasoft.testWork.model.request.Request;
import ru.vitasoft.testWork.model.request.RequestStatus;
import ru.vitasoft.testWork.repository.RequestRepository;


@Service
@RequiredArgsConstructor
public class RequestMapper {
    private final RequestRepository requestRepository;

    public Request toNewRequest(RequestDtoIn requestDtoIn) {
        return new Request(
                RequestStatus.DRAFT,
                requestDtoIn.getText(),
                requestDtoIn.getCreationDate(),
                requestDtoIn.getUser()
        );
    }


    public RequestDtoOut toRequestDtoOut(Request request) {
        Request fullRequestWithUser = requestRepository.getWithUser(request.getId());
        return RequestDtoOut.builder()
                .status(request.getStatus())
                .text(request.getText())
                .creationDate(request.getCreationDate())
                .user(UserMapper.toUserDto(fullRequestWithUser.getUser()))
                .build();
    }
}
