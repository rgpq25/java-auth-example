package com.renzo.auth_example.mail.exceptions;

public class MailSendingException extends RuntimeException {
    public MailSendingException(String message) {
        super(message);
    }
}
