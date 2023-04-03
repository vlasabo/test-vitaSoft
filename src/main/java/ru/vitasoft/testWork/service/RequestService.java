package ru.vitasoft.testWork.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.vitasoft.testWork.dto.request.RequestDtoIn;
import ru.vitasoft.testWork.dto.request.RequestDtoOut;
import ru.vitasoft.testWork.dto.request.RequestMapper;
import ru.vitasoft.testWork.exception.RequestUpdateException;
import ru.vitasoft.testWork.exception.UserAccessException;
import ru.vitasoft.testWork.model.request.Request;
import ru.vitasoft.testWork.model.request.RequestStatus;
import ru.vitasoft.testWork.model.user.User;
import ru.vitasoft.testWork.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private static final int PAGINATION_SIZE = 5;
    private final RequestRepository requestRepository;
    private final UserService userService;

    public RequestDtoOut addRequest(RequestDtoIn requestDto, String username) {
        requestDto.setCreationDate(LocalDateTime.now());
        requestDto.setUser(userService.findUserByUsername(username));
        return RequestMapper.toRequestDtoOut(requestRepository.save(RequestMapper.toRequest(requestDto)));
    }

    public void sendToSubmit(Long requestId, String username) {
        Request request = requestRepository.findById(requestId).orElseThrow();

        checkThatUserIsOwnerOfRequest(request.getUser(), userService.findUserByUsername(username));
        checkThatRequestStatusIsDraft(request);
        request.setStatus(RequestStatus.POSTED);
        requestRepository.save(request);
    }

    public RequestDtoOut editRequest(RequestDtoIn updatedRequest, Long requestId, String username) {
        Request requestFromBase = requestRepository.findById(requestId).orElseThrow();

        checkThatUserIsOwnerOfRequest(updatedRequest.getUser(), userService.findUserByUsername(username));
        checkThatRequestStatusIsDraft(requestFromBase);
        return RequestMapper.toRequestDtoOut(requestRepository.save(updateRequest(requestFromBase, updatedRequest)));
    }

    public Page<RequestDtoOut> getAllForUser(String username, Boolean dateDirection, Integer paginationFrom) {
        Long userId = userService.findUserByUsername(username).getId();
        Sort sort;
        if (!dateDirection) {
            sort = Sort.by(Sort.Direction.ASC, "creationDate");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "creationDate");
        }
        PageRequest pageRequest = PageRequest.of(paginationFrom, PAGINATION_SIZE, sort);
        Page<Request> requestsPage = requestRepository.findAllByUserIdIs(userId, pageRequest);

        var resultList = requestsPage.stream()
                .map(RequestMapper::toRequestDtoOut)
                .collect(Collectors.toList());
        return new PageImpl<>(resultList);
    }

    private Request updateRequest(Request requestFromBase, RequestDtoIn updatedRequest) {
        if (updatedRequest.getText() != null && !updatedRequest.getText().isBlank()) {
            requestFromBase.setText(updatedRequest.getText());
        } else {
            throw new RequestUpdateException(String.format("Text in request %s can't be empty!", requestFromBase.getId()));
        }
        return requestFromBase;
    }

    private void checkThatUserIsOwnerOfRequest(User userFromRequest, User userFromController) {
        if (!Objects.equals(userFromRequest, userFromController)) {
            throw new UserAccessException("user ".concat(userFromController.getUsername())
                    .concat(" cannot do something with this request"));
        }
    }

    private void checkThatRequestStatusIsDraft(Request request) {
        if (!Objects.equals(request.getStatus(), RequestStatus.DRAFT)) {
            throw new RequestUpdateException(String.format("request № %s status is not DRAFT", request.getId()));
        }
    }


}
