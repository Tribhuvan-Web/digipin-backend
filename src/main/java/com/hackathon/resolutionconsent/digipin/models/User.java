package com.hackathon.resolutionconsent.digipin.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must contain only alphanumeric characters and underscores")
    private String userName;
    
    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(unique = true, nullable = true)
    private String emailId;

    
    @Column(nullable = false)
    private String password; 
    
    @Column(unique = true, length = 12)
    private String aadhaarNumber; 
    
    @Column(nullable = false)
    private boolean isAadhaarVerified = false; 

}
