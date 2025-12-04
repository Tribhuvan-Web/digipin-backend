package com.hackathon.resolutionconsent.digipin.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class DigitalAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String suffix;
    private String digitalAddress;
    private String generatedDigipin;
    private Long userId;

    private Double latitude;
    private Double longitude;
    private String address;
    private String pinCode;
    private String purpose;
    private String addressName;
    private LocalDateTime createdAt;

    @Column
    private Long activeConsentId;

    @Column
    private boolean hasActiveConsent = false;

    @Column
    private Integer totalRatings = 0;

    @Column
    private Integer totalRatingScore = 0;

    @Column
    private Double averageRating = 0.0;

    @Column
    private Double confidenceScore = 50.0;

    @Column
    private Integer totalFulfillments = 0;

    @Column
    private Boolean isAavaVerified = false;

    @Column
    @Enumerated(EnumType.STRING)
    private VerificationType verificationType = VerificationType.AADHAAR_ONLY;

    @Column
    private String aavaAgentId;

    @Column
    private LocalDateTime aavaVerifiedAt;

    @Column(length = 1000)
    private String aavaVerificationNotes;

    @Column
    private Boolean requiresAavaVerification = false;


    public enum VerificationType {
        AADHAAR_ONLY,
        AADHAAR_PLUS_CONFIDENCE,
        AAVA
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
