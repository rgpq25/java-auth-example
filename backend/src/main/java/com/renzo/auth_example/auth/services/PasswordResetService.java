package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.auth.exceptions.InvalidVerificationCodeException;
import com.renzo.auth_example.auth.exceptions.VerificationExpiredException;
import com.renzo.auth_example.auth.models.Account;
import com.renzo.auth_example.auth.models.Verification;
import com.renzo.auth_example.mail.MailService;
import com.renzo.auth_example.mail.exceptions.MailSendingException;
import com.renzo.auth_example.user.exceptions.UserNotFoundException;
import com.renzo.auth_example.user.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PasswordResetService {
    private final AccountService accountService;
    private final VerificationService verificationService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            UserService userService,
            AccountService accountService,
            VerificationService verificationService,
            MailService mailService,
            PasswordEncoder passwordEncoder
    ) {
        this.accountService = accountService;
        this.verificationService = verificationService;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void sendPasswordResetEmail(String email) {
        verificationService.deleteAllPasswordResetVerifications(email);
        String passwordResetCode = verificationService.createPasswordResetVerification(email);

        try {
            mailService.sendPasswordResetUrl(email, passwordResetCode);
        } catch (MailSendingException ignored) {}
    }

    public void resetPassword(String code, String email, String password) {
        Verification pendingVerification = verificationService.getPendingVerification(email, code, Verification.VerificationType.PASSWORD_RESET)
                .orElseThrow(() -> new InvalidVerificationCodeException("Invalid password reset code."));

        if (pendingVerification.getExpiresAt().before(new Date())) {
            throw new VerificationExpiredException("Password reset code has expired.");
        }

        Account account = accountService.findByEmailAndProviderId(email, Account.ProviderType.CREDENTIALS)
                .orElseThrow(() -> new UserNotFoundException("email", email));
        account.setPassword(passwordEncoder.encode(password));
        accountService.updateAccount(account);
        verificationService.delete(pendingVerification);
    }
}
