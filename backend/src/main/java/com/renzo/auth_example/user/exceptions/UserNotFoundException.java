package com.renzo.auth_example.user.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(String field, Object value) {
        super(String.format("User not found with %s: %s", field, value));
    }
}
