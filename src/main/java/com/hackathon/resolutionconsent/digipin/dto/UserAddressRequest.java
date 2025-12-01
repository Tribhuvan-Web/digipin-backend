package com.hackathon.resolutionconsent.digipin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserAddressRequest {
    
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    
    @NotBlank(message = "Digital address is required")
    private String digitalAddress;
    
    @NotBlank(message = "UPI PIN is required")
    private String upiPin;
}
