package com.renzo.auth_example.auth.models;

import com.renzo.auth_example.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "verifications")
public class Verification extends BaseEntity {
    public enum VerificationType {
        EMAIL_VERIFICATION,
        PASSWORD_RESET
    }

    @Column(nullable = false)
    private VerificationType verificationType;

    @Column(nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private Date expiresAt;

    public Verification() {  }

    public Verification(VerificationType verificationType, String identifier, String value, Date expiresAt) {
        this.verificationType = verificationType;
        this.identifier = identifier;
        this.value = value;
        this.expiresAt = expiresAt;
    }

    public VerificationType getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(VerificationType verificationType) {
        this.verificationType = verificationType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
