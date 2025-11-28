package com.hackathon.resolutionconsent.digipin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDigitalAddressRequest {
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    private String address; 
}
