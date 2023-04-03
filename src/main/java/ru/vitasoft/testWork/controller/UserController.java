package ru.vitasoft.testWork.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import ru.vitasoft.testWork.dto.request.RequestDtoIn;
import ru.vitasoft.testWork.dto.request.RequestDtoOut;
import ru.vitasoft.testWork.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final RequestService requestService;

    @PostMapping("/add")
    //@PreAuthorize() //TODO: настроить по всему контроллеру потом
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDtoOut addRequest(@Valid @RequestBody RequestDtoIn request,
                                    @AuthenticationPrincipal User user) {
        log.debug("add new request {}", request);
        return requestService.addRequest(request, user.getUsername());
    }

    @PostMapping("/sendToSubmit/{requestId}")
    //@PreAuthorize()
    @ResponseStatus(HttpStatus.OK)
    public void sendToSubmit(@PathVariable Long requestId,
                             @AuthenticationPrincipal User user) {
        log.debug("sending request №{} to submit", requestId);
        requestService.sendToSubmit(requestId, user.getUsername());
    }

    @PutMapping("/edit/{requestId}")
    //@PreAuthorize()
    @ResponseStatus(HttpStatus.OK)
    public RequestDtoOut editRequest(@Valid @RequestBody RequestDtoIn request,
                                     @PathVariable Long requestId,
                                     @AuthenticationPrincipal User user) {
        log.debug("edit request №{}", requestId);
        return requestService.editRequest(request, requestId, user.getUsername());
    }

    @GetMapping("/all")
    //@PreAuthorize()
    @ResponseStatus(HttpStatus.OK)
    public Page<RequestDtoOut> getUserRequests(@RequestParam(defaultValue = "false") Boolean dateDirection,
                                               @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer paginationFrom,
                                               @AuthenticationPrincipal User user) {
        log.debug("get all requests for user {}", user.getUsername());
        return requestService.getAllForUser(user.getUsername(), dateDirection, paginationFrom);
    }
}
