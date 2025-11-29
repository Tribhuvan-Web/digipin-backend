package com.hackathon.resolutionconsent.digipin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateDigitalAddressRequest {
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    private String address; 
    
    @NotBlank(message = "UPI PIN is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "UPI PIN must be exactly 6 digits")
    private String upiPin;
    
    @NotBlank(message = "Consent type is required")
    @Pattern(regexp = "^(PERMANENT|TEMPORARY)$", message = "Consent type must be either PERMANENT or TEMPORARY")
    private String consentType;
    
    private Integer consentDurationDays;
}
