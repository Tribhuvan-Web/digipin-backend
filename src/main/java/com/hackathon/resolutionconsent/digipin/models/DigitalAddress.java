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
    private Double averageRating = 0.0; // Average rating (0-5) from user ratings

    @Column
    private Double confidenceScore = 50.0; // Dynamic trust score (0-100), baseline is 50

    @Column
    private Integer totalFulfillments = 0; // Count of service fulfillment feedbacks

    // AAVA (Address Authentication and Verification Agent) fields
    @Column
    private Boolean isAavaVerified = false; // Physical verification by government agent

    @Column
    @Enumerated(EnumType.STRING)
    private VerificationType verificationType = VerificationType.AADHAAR_ONLY; // Default to Aadhaar verification

    @Column
    private String aavaAgentId; // Government agent who performed verification

    @Column
    private LocalDateTime aavaVerifiedAt; // Timestamp of physical verification

    @Column(length = 1000)
    private String aavaVerificationNotes; // Agent's verification observations

    @Column
    private Boolean requiresAavaVerification = false; // Flag for high-security use cases

    public enum VerificationType {
        AADHAAR_ONLY,           // Basic Aadhaar verification only
        AADHAAR_PLUS_CONFIDENCE, // Aadhaar + confidence score from fulfillments
        AAVA                    // Physical verification by agent (highest trust)
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
