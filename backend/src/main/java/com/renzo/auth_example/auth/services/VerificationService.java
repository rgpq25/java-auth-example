package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.auth.models.Verification;
import com.renzo.auth_example.auth.repositories.VerificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class VerificationService {
    private final VerificationRepository verificationRepository;

    public VerificationService(VerificationRepository verificationRepository) {
        this.verificationRepository = verificationRepository;
    }

    public Optional<Verification> getPendingVerification(
            String identifier,
            String rawToken,
            Verification.VerificationType verificationType
    ) {
        String hashedToken = sha256Hex(rawToken);
        return verificationRepository.findFirstByIdentifierAndValueAndVerificationType(identifier, hashedToken, verificationType);
    }

    public String createEmailVerification(String identifier) {
        String rawToken = generateRaw6DigitToken();
        String hashedToken = sha256Hex(rawToken);
        Date expiresAt = new Date(System.currentTimeMillis() + 300000); // 5 minutes

        Verification emailVerification = new Verification(
                Verification.VerificationType.EMAIL_VERIFICATION,
                identifier,
                hashedToken,
                expiresAt
        );
        verificationRepository.save(emailVerification);

        return rawToken;
    }

    public String createPasswordResetVerification(String identifier) {
        String rawToken = generateRawLongToken();
        String hashedToken = sha256Hex(rawToken);
        Date expiresAt = new Date(System.currentTimeMillis() + 300000); // 5 minutes

        Verification passwordResetVerification = new Verification(
                Verification.VerificationType.PASSWORD_RESET,
                identifier,
                hashedToken,
                expiresAt
        );
        verificationRepository.save(passwordResetVerification);

        return rawToken;
    }

    @Transactional
    public void delete(Verification verification) {
        verificationRepository.deleteById(verification.getId());
    }

    @Transactional
    public void deleteAllEmailVerifications(String identifier) {
        verificationRepository.deleteByIdentifierAndVerificationType(identifier, Verification.VerificationType.EMAIL_VERIFICATION);
    }

    @Transactional
    public void deleteAllPasswordResetVerifications(String identifier) {
        verificationRepository.deleteByIdentifierAndVerificationType(identifier, Verification.VerificationType.PASSWORD_RESET);
    }

    private String generateRaw6DigitToken() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private String generateRawLongToken() {
        byte[] bytes = new byte[32]; // 256-bit token
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
