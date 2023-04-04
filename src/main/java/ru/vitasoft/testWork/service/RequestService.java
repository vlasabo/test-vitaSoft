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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private static final int PAGINATION_SIZE = 5;
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final RequestMapper requestMapper;

    public RequestDtoOut addRequest(RequestDtoIn requestDto, String username) {
        requestDto.setCreationDate(LocalDateTime.now());
        requestDto.setUser(userService.findUserByUsername(username));
        return requestMapper.toRequestDtoOut(requestRepository.save(requestMapper.toNewRequest(requestDto)));
    }

    public void sendToSubmit(Long requestId, String username) {
        Request request = requestRepository.findById(requestId).orElseThrow();

        checkThatUserIsOwnerOfRequest(requestRepository.getWithUser(requestId).getUser(), userService.findUserByUsername(username));
        checkThatRequestStatusIsDraft(request);
        request.setStatus(RequestStatus.POSTED);
        requestRepository.save(request);
    }

    public RequestDtoOut editRequest(RequestDtoIn updatedRequest, Long requestId, String username) {
        Request requestFromBase = requestRepository.findById(requestId).orElseThrow();

        checkThatUserIsOwnerOfRequest(requestRepository.getWithUser(requestId).getUser(), userService.findUserByUsername(username));
        checkThatRequestStatusIsDraft(requestFromBase);
        return requestMapper.toRequestDtoOut(requestRepository.save(updateRequest(requestFromBase, updatedRequest)));
    }

    public Page<RequestDtoOut> getAllForUser(String username, Boolean dateDirection, Integer paginationFrom) {
        Long userId = userService.findUserByUsername(username).getId();
        Sort sort = getSort(dateDirection);
        PageRequest pageRequest = PageRequest.of(paginationFrom, PAGINATION_SIZE, sort);
        Page<Request> requestsPage = requestRepository.findAllByUserIdIs(userId, pageRequest);

        var resultList = requestsPage.stream()
                .map(requestMapper::toRequestDtoOut)
                .collect(Collectors.toList());
        return new PageImpl<>(resultList);
    }

    public RequestDtoOut getRequest(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        if (!RequestStatus.POSTED.equals(request.getStatus())) {
            throw new UserAccessException("attempt to view a request which status is not POSTED!");
        }
        request.setText(setTextToOperator(request.getText()));
        return requestMapper.toRequestDtoOut(request);
    }

    public Page<RequestDtoOut> getAllForOperator(Boolean dateDirection, Integer paginationFrom) {
        Sort sort = getSort(dateDirection);
        PageRequest pageRequest = PageRequest.of(paginationFrom, PAGINATION_SIZE, sort);
        Page<Request> requestsPage = requestRepository.findAllByStatusIs(pageRequest, RequestStatus.POSTED);

        List<RequestDtoOut> resultList = getRequestDtoOutsForOperator(requestsPage);
        return new PageImpl<>(resultList);
    }

    public void changeStatus(Long requestId, RequestStatus status) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        if (!RequestStatus.POSTED.equals(request.getStatus())) {
            throw new RequestUpdateException(
                    "to accept or reject a request, the status of the request must be POSTED!"
            );
        }
        request.setStatus(status);
        requestRepository.save(request);
    }

    public Page<RequestDtoOut> getAllUserRequestForOperator(Boolean dateDirection, Integer paginationFrom, String username) {
        List<User> users = userService.findUserByPartOfUsername(username);
        if (users.size() == 0) {
            throw new IllegalArgumentException("zero users find by part of username " + username);
        }
        if (users.size() > 1) {
            throw new IllegalArgumentException("too many users find by part of username " + username);
        }

        User user = users.get(0);
        Sort sort = getSort(dateDirection);
        PageRequest pageRequest = PageRequest.of(paginationFrom, PAGINATION_SIZE, sort);
        Page<Request> requestsPage =
                requestRepository.findAllByStatusIsAndUserIdIs(pageRequest, RequestStatus.POSTED, user.getId());

        List<RequestDtoOut> resultList = getRequestDtoOutsForOperator(requestsPage);
        return new PageImpl<>(resultList);
    }

    private List<RequestDtoOut> getRequestDtoOutsForOperator(Page<Request> requestsPage) {
        var resultList = requestsPage.stream()
                .map(requestMapper::toRequestDtoOut)
                .collect(Collectors.toList());
        resultList.forEach(requestDtoOut ->
                requestDtoOut.setText(setTextToOperator(requestDtoOut.getText())));
        return resultList;
    }

    private Sort getSort(Boolean dateDirection) {
        Sort sort;
        if (!dateDirection) {
            sort = Sort.by(Sort.Direction.ASC, "creationDate");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "creationDate");
        }
        return sort;
    }

    private String setTextToOperator(String text) {
        StringJoiner stringJoiner = new StringJoiner("-");
        Arrays.stream(text.split(""))
                .forEachOrdered(stringJoiner::add);
        return stringJoiner.toString();
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
