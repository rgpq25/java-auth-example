package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.auth.models.Verification;
import com.renzo.auth_example.auth.repositories.VerificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
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
            String code,
            Verification.VerificationType verificationType
    ) {
        return verificationRepository.findFirstByIdentifierAndVerificationTypeAndValue(identifier, verificationType, code);
    }

    public String createEmailVerification(String identifier) {
        String rawCode = generateRawCode();
        Date expiresAt = new Date(System.currentTimeMillis() + 300000); // 5 minutes

        Verification emailVerification = new Verification(
                Verification.VerificationType.EMAIL_VERIFICATION,
                identifier,
                rawCode, // TODO: Hash the generatedToken
                expiresAt
        );
        verificationRepository.save(emailVerification);

        return rawCode;
    }

    public String createPasswordResetVerification(String identifier) {
        String rawCode = generateRawCode();
        Date expiresAt = new Date(System.currentTimeMillis() + 300000); // 5 minutes

        Verification passwordResetVerification = new Verification(
                Verification.VerificationType.PASSWORD_RESET,
                identifier,
                rawCode, // TODO: Hash the generatedToken
                expiresAt
        );
        verificationRepository.save(passwordResetVerification);

        return rawCode;
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

    private String generateRawCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
