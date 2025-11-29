package com.hackathon.resolutionconsent.digipin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsentResponse {
    private String consentToken;
    private String consentType;
    private String expiresAt;
    private String message;
}
