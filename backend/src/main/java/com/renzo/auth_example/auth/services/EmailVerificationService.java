package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.auth.exceptions.InvalidVerificationCodeException;
import com.renzo.auth_example.auth.exceptions.VerificationExpiredException;
import com.renzo.auth_example.auth.models.Verification;
import com.renzo.auth_example.mail.MailService;
import com.renzo.auth_example.mail.exceptions.MailSendingException;
import com.renzo.auth_example.user.exceptions.UserNotFoundException;
import com.renzo.auth_example.user.models.User;
import com.renzo.auth_example.user.services.UserService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EmailVerificationService {
    private final UserService userService;
    private final VerificationService verificationService;
    private final MailService mailService;

    public EmailVerificationService(
            UserService userService,
            VerificationService verificationService,
            MailService mailService
    ) {
        this.userService = userService;
        this.verificationService = verificationService;
        this.mailService = mailService;
    }

    public void sendVerificationEmail(String email) {
        verificationService.deleteAllEmailVerifications(email);
        String token = verificationService.createEmailVerification(email);

        try {
            mailService.sendEmailVerificationToken(email, token);
        } catch (MailSendingException ignored) {}
    }

    public void verifyEmail(String token, String email) {
        Verification pendingVerification = verificationService.getPendingVerification(email, token, Verification.VerificationType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new InvalidVerificationCodeException("Invalid email verification token."));

        if (pendingVerification.getExpiresAt().before(new Date())) {
            throw new VerificationExpiredException("Email verification token has expired.");
        }

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));
        user.setEmailVerified(true);
        userService.updateUser(user);
        verificationService.delete(pendingVerification);
    }
}
