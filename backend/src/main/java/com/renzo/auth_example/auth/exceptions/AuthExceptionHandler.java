package com.renzo.auth_example.auth.exceptions;

import com.renzo.auth_example.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackages = "com.renzo.auth_example.auth")
public class AuthExceptionHandler {
    @ExceptionHandler(VerificationExpiredException.class)
    public ResponseEntity<ErrorResponse> handleVerificationExpired(VerificationExpiredException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Verification code has expired.");
        errorResponse.setErrors(List.of(ex.getMessage()));

        return ResponseEntity.status(HttpStatus.GONE).body(errorResponse);
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVerificationCode(InvalidVerificationCodeException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Invalid verification code.");
        errorResponse.setErrors(List.of(ex.getMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
