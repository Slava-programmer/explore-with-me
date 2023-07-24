package ru.practicum.mainservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Setter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private String message;

    private Map<String, Object> errors;

    private String reason;

    private String status;

    private LocalDateTime timestamp;
}
