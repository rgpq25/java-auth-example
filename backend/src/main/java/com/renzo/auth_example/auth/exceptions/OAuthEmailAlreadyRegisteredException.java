package com.renzo.auth_example.auth.exceptions;

public class OAuthEmailAlreadyRegisteredException extends RuntimeException {
    public OAuthEmailAlreadyRegisteredException(String email) {
        super("Email already registered with credentials provider: " + email);
    }
}
