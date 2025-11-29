package com.hackathon.resolutionconsent.digipin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileDto {
    private String username;
    private String phoneNumber;
    private String emailId;
    private boolean isAadhaarVerified;
}
