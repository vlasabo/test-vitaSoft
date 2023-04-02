package ru.vitasoft.testWork.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import ru.vitasoft.testWork.dto.request.RequestDtoIn;
import ru.vitasoft.testWork.dto.request.RequestDtoOut;
import ru.vitasoft.testWork.service.RequestService;

import javax.validation.Valid;


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
    }
}
