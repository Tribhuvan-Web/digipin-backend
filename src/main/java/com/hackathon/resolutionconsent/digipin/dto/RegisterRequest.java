package com.hackathon.resolutionconsent.digipin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String userName;
    
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    
    @Email(message = "Invalid email format")
    private String emailId;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
