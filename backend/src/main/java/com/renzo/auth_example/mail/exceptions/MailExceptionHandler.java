package com.renzo.auth_example.mail.exceptions;

import com.renzo.auth_example.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackages = "com.renzo.auth_example.mail")
public class MailExceptionHandler {
    @ExceptionHandler(MailSendingException.class)
    public ResponseEntity<ErrorResponse> handleMailSendingException(MailSendingException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Could not send verification email.");
        errorResponse.setErrors(List.of(ex.getMessage()));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
