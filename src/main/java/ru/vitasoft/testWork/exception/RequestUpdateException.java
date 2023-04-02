package ru.vitasoft.testWork.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RequestUpdateException extends RuntimeException {
    private final String reason;
}
