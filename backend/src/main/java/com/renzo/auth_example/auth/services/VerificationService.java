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
            Verification.VerificationType verificationType,
            String code
    ) {
        return verificationRepository.findFirstByIdentifierAndVerificationTypeAndValue(identifier, verificationType, code);
    }

    @Transactional
    public void delete(Verification verification) {
        verificationRepository.deleteById(verification.getId());
    }

    public String createEmailVerification(String identifier) {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000;
        String rawCode = String.valueOf(code);
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

    @Transactional
    public void deleteAllEmailVerifications(String identifier) {
        verificationRepository.deleteByIdentifierAndVerificationType(identifier, Verification.VerificationType.EMAIL_VERIFICATION);
    }
}
