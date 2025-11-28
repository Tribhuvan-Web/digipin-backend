package com.hackathon.resolutionconsent.digipin.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String userName;
    
    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(unique = true, nullable = true)
    private String emailId;

    
    @Column(nullable = false)
    private String password; // Encrypted password
    
    @Column(unique = true, length = 12)
    private String aadhaarNumber; // 12-digit Aadhaar number (verified)
    
    @Column(nullable = false)
    private boolean isAadhaarVerified = false; // Aadhaar verification status

}
