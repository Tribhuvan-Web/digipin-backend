package com.hackathon.resolutionconsent.digipin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ResolveAddressWithConsentRequest {
    
    @NotBlank(message = "Digital address is required")
    private String digitalAddress;
    
    @NotBlank(message = "UPI PIN is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "UPI PIN must be exactly 6 digits")
    private String upiPin;
}
