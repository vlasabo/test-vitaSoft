package ru.vitasoft.testWork.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDtoOut addRequest(@Valid @RequestBody RequestDtoIn request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("add new request {}", request);
        return requestService.addRequest(request, userDetails.getUsername());
    }

    @PatchMapping("/sendToSubmit/{requestId}")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.OK)
    public void sendToSubmit(@PathVariable Long requestId,
                             @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("sending request №{} to submit", requestId);
        requestService.sendToSubmit(requestId, userDetails.getUsername());
    }

    @PatchMapping("/edit/{requestId}")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.OK)
    public RequestDtoOut editRequest(@Valid @RequestBody RequestDtoIn request,
                                     @PathVariable Long requestId,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("edit request №{}", requestId);
        return requestService.editRequest(request, requestId, userDetails.getUsername());
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.OK)
    public Page<RequestDtoOut> getUserRequests(@RequestParam(defaultValue = "false") Boolean dateDirection,
                                               @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer paginationFrom,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("get all requests for user {}", userDetails.getUsername());
        return requestService.getAllForUser(userDetails.getUsername(), dateDirection, paginationFrom);
    }
}
