package ru.vitasoft.testWork.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.vitasoft.testWork.dto.request.RequestDtoOut;
import ru.vitasoft.testWork.model.request.RequestStatus;
import ru.vitasoft.testWork.service.RequestService;

import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/operator")
@RequiredArgsConstructor
@Slf4j
public class OperatorController {
    private final RequestService requestService;

    @GetMapping("/request/{requestId}")
    //@PreAuthorize() //todo раскомментить после донастройки
    public RequestDtoOut getRequest(@PathVariable Long requestId) {
        return requestService.getRequest(requestId);
    }

    @GetMapping("/all")
    //@PreAuthorize()
    @ResponseStatus(HttpStatus.OK)
    public Page<RequestDtoOut> getAllRequests(@RequestParam(defaultValue = "false") Boolean dateDirection,
                                              @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer paginationFrom) {
        log.debug("get all requests from operator");
        return requestService.getAllForOperator(dateDirection, paginationFrom);
    }

    @GetMapping("/all/byUser")
    //@PreAuthorize()
    @ResponseStatus(HttpStatus.OK)
    public Page<RequestDtoOut> getUserRequests(@RequestParam(defaultValue = "false") Boolean dateDirection,
                                               @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer paginationFrom,
                                               @RequestParam String username) {
        log.debug("get all requests from user {} from operator", username);
        return requestService.getAllUserRequestForOperator(dateDirection, paginationFrom, username);
    }

    @PatchMapping("/accept/{requestId}")
    //@PreAuthorize()
    @ResponseStatus(HttpStatus.OK)
    public void acceptRequest(@PathVariable Long requestId) {
        log.debug("accept request №{}", requestId);
        requestService.changeStatus(requestId, RequestStatus.ACCEPTED);
    }

    @PatchMapping("/reject/{requestId}")
    //@PreAuthorize()
    @ResponseStatus(HttpStatus.OK)
    public void rejectRequest(@PathVariable Long requestId) {
        log.debug("reject request №{}", requestId);
        requestService.changeStatus(requestId, RequestStatus.REJECTED);
    }
}
