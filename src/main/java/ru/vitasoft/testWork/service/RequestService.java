package ru.vitasoft.testWork.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vitasoft.testWork.dto.request.RequestDtoIn;
import ru.vitasoft.testWork.dto.request.RequestDtoOut;
import ru.vitasoft.testWork.dto.request.RequestMapper;
import ru.vitasoft.testWork.exception.RequestUpdateException;
import ru.vitasoft.testWork.exception.UserAccessException;
import ru.vitasoft.testWork.model.request.Request;
import ru.vitasoft.testWork.model.request.RequestStatus;
import ru.vitasoft.testWork.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;

    public RequestDtoOut addRequest(RequestDtoIn requestDto, String username) {
        requestDto.setCreationDate(LocalDateTime.now());
        requestDto.setUser(userService.findUserByUsername(username));
        return RequestMapper.toRequestDtoOut(requestRepository.save(RequestMapper.toRequest(requestDto)));
    }

    public void sendToSubmit(Long requestId, String username) {
        Request request = requestRepository.findById(requestId).orElseThrow();

        if (!Objects.equals(request.getUser(), userService.findUserByUsername(username))) {
            throw new UserAccessException("user ".concat(username).concat(" cannot send someone else's request"));
        }
        if (!Objects.equals(request.getStatus(), RequestStatus.DRAFT)) {
            throw new RequestUpdateException("request №".concat(requestId.toString()).concat(" status is not DRAFT"));
        }
        request.setStatus(RequestStatus.POSTED);
        requestRepository.save(request);
    }
}
