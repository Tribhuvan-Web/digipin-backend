package com.hackathon.resolutionconsent.digipin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDigitalAddressRequest {
    private Long userId; 
    
    private String username; 
    
    private String digipin;
    
    @NotBlank(message = "Suffix is required")
    private String suffix; 
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    private String address; 
}
