package ru.practicum.stat.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
public class ApiError {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private final ZonedDateTime timestamp;
    private final HttpStatus status;
    private final String message;
    private final String path;

    public ApiError(HttpStatus status, String message, String path) {
        this.timestamp = ZonedDateTime.now();
        this.status = status;
        this.message = message;
        this.path = path;
    }
}
