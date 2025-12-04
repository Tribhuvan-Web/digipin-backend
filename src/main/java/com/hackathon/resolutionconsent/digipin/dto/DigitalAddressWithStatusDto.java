package com.hackathon.resolutionconsent.digipin.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DigitalAddressWithStatusDto {
    // Address Info
    private Long id;
    private String digitalAddress;
    private String generatedDigipin;
    private String suffix;
    private String addressName;
    private String address;
    private String pinCode;
    private String purpose;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    
    private boolean isActive;
    private boolean isExpired;
    private String linkStatus; 
    
    private String consentType; 
    private boolean isPermanent;
    private boolean isTemporary;
    
    private LocalDateTime expiresAt;
    private Long daysRemaining;
    
    private Boolean isAavaVerified;
    private Double confidenceScore;
}
