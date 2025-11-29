package com.hackathon.resolutionconsent.digipin.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class DigitalAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String suffix; 
    private String digitalAddress; 
    private String generatedDigipin; 
    private Long userId;
    
    private Double latitude;
    private Double longitude;
    private String address; 
    
    private LocalDateTime createdAt;
    
    @Column
    private Long activeConsentId; 
    
    @Column
    private boolean hasActiveConsent = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
