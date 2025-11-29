package com.hackathon.resolutionconsent.digipin.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long digitalAddressId;

    @Column(nullable = false, length = 60)
    private String upiPinHash; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentType consentType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime expiresAt; 

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(length = 6)
    private String consentToken; // 6-character token for easy memorization 

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (consentType == ConsentType.TEMPORARY && expiresAt == null) {
            expiresAt = LocalDateTime.now().plusDays(30);
        }
    }

    public enum ConsentType {
        PERMANENT,
        TEMPORARY
    }
}
