package com.hackathon.resolutionconsent.digipin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolveAddressWithConsentRequest {
    
    @NotBlank(message = "Digital address is required")
    private String digitalAddress;
    
    @NotBlank(message = "Consent token is required")
    private String consentToken;
}
