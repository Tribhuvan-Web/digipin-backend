package com.hackathon.resolutionconsent.digipin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    
    @NotBlank(message = "Email or phone number is required")
    private String emailOrPhone;
    
    @NotBlank(message = "Aadhaar number is required")
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must be exactly 12 digits")
    private String aadhaarNumber;
    
    @NotBlank(message = "Date of birth is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date of birth must be in format YYYY-MM-DD")
    private String dateOfBirth;
    
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
