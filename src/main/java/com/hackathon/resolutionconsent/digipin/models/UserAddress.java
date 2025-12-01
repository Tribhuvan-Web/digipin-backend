package com.hackathon.resolutionconsent.digipin.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_addresses")
public class UserAddress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;
    
    @Column(nullable = false)
    private String digitalAddress;
    
    @Column(nullable = false)
    private String upiPin;
}
