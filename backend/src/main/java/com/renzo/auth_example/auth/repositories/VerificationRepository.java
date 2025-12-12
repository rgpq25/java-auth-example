package com.renzo.auth_example.auth.repositories;

import com.renzo.auth_example.auth.models.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {
    Optional<Verification> findFirstByValueAndVerificationType(String value, Verification.VerificationType verificationType);
    void deleteByIdentifierAndVerificationType(String identifier, Verification.VerificationType verificationType);
}
