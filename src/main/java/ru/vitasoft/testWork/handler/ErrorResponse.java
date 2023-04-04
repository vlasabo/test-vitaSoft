package ru.vitasoft.testWork.handler;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private final String reason;
    private final LocalDateTime timestamp;
    private final String status;
}
