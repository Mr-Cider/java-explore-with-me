package ru.practicum.exception;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ApiError {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;
    private final String status;
    private final String reason;
    private final String message;
    private final List<String> errors;

    public ApiError(ErrorStatus status, String reason, String message, List<String> errors) {
        this.status = status.getStatus();
        this.reason = reason;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(ErrorStatus status, String reason, String message) {
        this(status, reason, message, null);
    }
}