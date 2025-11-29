package com.hackathon.resolutionconsent.digipin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResolveAddressResponse {
    private Boolean success;
    private String digitalAddress;
    private String address;  
    private Double latitude;
    private Double longitude;
    private String generatedDigipin;
    private String partnerName;
    private String scope;
    private String message;
}
