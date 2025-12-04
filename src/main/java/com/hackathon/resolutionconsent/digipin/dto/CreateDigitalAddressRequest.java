package com.hackathon.resolutionconsent.digipin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateDigitalAddressRequest {
    private Long userId; 
    
    private String username; 
    
    private String digipin;
    
    private String digitalAddress; 
    
    @NotBlank(message = "Suffix is required")
    private String suffix; 
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    private String address; 
    private String addressName;
    private String pincode;
    private String purpose;
    
    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "PIN must be exactly 6 digits")
    private String uniPin;
    
    @NotBlank(message = "Consent type is required")
    @Pattern(regexp = "^(PERMANENT|TEMPORARY)$", message = "Consent type must be either PERMANENT or TEMPORARY")
    private String consentType;
    
    private Integer consentDurationDays; 
}
