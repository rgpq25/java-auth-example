package com.renzo.auth_example.common;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp = LocalDateTime.now();

    protected ErrorResponse() {}

    protected ErrorResponse(String message, List<String> errors) {
        this.message = message;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
