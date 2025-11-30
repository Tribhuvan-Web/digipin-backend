package com.hackathon.resolutionconsent.digipin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AavaVerificationRequest {

    @NotBlank(message = "Digital address is required")
    private String digitalAddress;

    @NotBlank(message = "AAVA agent ID is required")
    private String agentId;

    @NotNull(message = "Verification status is required")
    private VerificationStatus verificationStatus;

    @NotNull(message = "Location confirmation is required")
    private Boolean locationConfirmed;

    private String verificationNotes;

    private Double verifiedLatitude;

    private Double verifiedLongitude;

    private String photoproofUrl;

    public enum VerificationStatus {
        VERIFIED,
        VERIFICATION_FAILED,
        PENDING,
        REQUIRES_CORRECTION
    }
}
