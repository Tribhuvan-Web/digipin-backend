package com.hackathon.resolutionconsent.digipin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolveAddressRequest {
    @NotBlank(message = "Token is required")
    private String token; // JWT consent token
    
    @NotBlank(message = "Digital address is required")
    private String digitalAddress; // e.g., tribhuvan@home.dop
}
