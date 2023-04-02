package ru.vitasoft.testWork.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserAccessException extends RuntimeException {
    private final String reason;
}
