package ru.vitasoft.testWork.handler;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.vitasoft.testWork.exception.RequestUpdateException;
import ru.vitasoft.testWork.exception.UserAccessException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(ChangeSetPersister.NotFoundException e) {
        return new ErrorResponse(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.getReasonPhrase()
        );
    }

    @ExceptionHandler(RequestUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(RequestUpdateException e) {
        return new ErrorResponse(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.getReasonPhrase()
        );
    }

    @ExceptionHandler(UserAccessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(UserAccessException e) {
        return new ErrorResponse(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.CONFLICT.getReasonPhrase()
        );
    }
}
