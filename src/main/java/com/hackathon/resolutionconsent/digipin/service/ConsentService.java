package com.hackathon.resolutionconsent.digipin.service;

import com.hackathon.resolutionconsent.digipin.models.Consent;
import com.hackathon.resolutionconsent.digipin.models.DigitalAddress;
import com.hackathon.resolutionconsent.digipin.repository.ConsentRepository;
import com.hackathon.resolutionconsent.digipin.repository.DigitalAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConsentService {

    @Autowired
    private ConsentRepository consentRepository;

    @Autowired
    private DigitalAddressRepository digitalAddressRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Create a new consent with UPI PIN
     */
    @Transactional
    public Consent createConsent(Long userId, Long digitalAddressId, String upiPin, 
                                 Consent.ConsentType consentType, Integer durationDays) {
        
        // Validate UPI PIN format (6 digits)
        if (upiPin == null || !upiPin.matches("^[0-9]{6}$")) {
            throw new IllegalArgumentException("UPI PIN must be exactly 6 digits");
        }

        // Deactivate any existing consents for this digital address
        Optional<Consent> existingConsent = consentRepository.findByDigitalAddressIdAndIsActiveTrue(digitalAddressId);
        existingConsent.ifPresent(consent -> {
            consent.setActive(false);
            consentRepository.save(consent);
        });

        // Create new consent - Use UPI PIN as consent token (hashed for security)
        Consent consent = new Consent();
        consent.setUserId(userId);
        consent.setDigitalAddressId(digitalAddressId);
        consent.setUpiPinHash(passwordEncoder.encode(upiPin));
        consent.setConsentType(consentType);
        consent.setConsentToken(upiPin); // Store the UPI PIN as the consent token

        if (consentType == Consent.ConsentType.TEMPORARY) {
            int days = (durationDays != null && durationDays > 0) ? durationDays : 30;
            consent.setExpiresAt(LocalDateTime.now().plusDays(days));
        }

        Consent savedConsent = consentRepository.save(consent);

        // Update digital address with active consent
        DigitalAddress digitalAddress = digitalAddressRepository.findById(digitalAddressId)
                .orElseThrow(() -> new RuntimeException("Digital address not found"));
        digitalAddress.setActiveConsentId(savedConsent.getId());
        digitalAddress.setHasActiveConsent(true);
        digitalAddressRepository.save(digitalAddress);

        return savedConsent;
    }

    /**
     * Verify UPI PIN for a digital address
     */
    public boolean verifyUpiPin(Long digitalAddressId, String upiPin) {
        Optional<Consent> consentOpt = consentRepository.findByDigitalAddressIdAndIsActiveTrue(digitalAddressId);
        
        if (consentOpt.isEmpty()) {
            return false;
        }

        Consent consent = consentOpt.get();

        // Check if consent is expired (for temporary consents)
        if (consent.getConsentType() == Consent.ConsentType.TEMPORARY) {
            if (consent.getExpiresAt() != null && consent.getExpiresAt().isBefore(LocalDateTime.now())) {
                // Deactivate expired consent
                consent.setActive(false);
                consentRepository.save(consent);

                // Update digital address
                DigitalAddress digitalAddress = digitalAddressRepository.findById(digitalAddressId).orElse(null);
                if (digitalAddress != null) {
                    digitalAddress.setHasActiveConsent(false);
                    digitalAddress.setActiveConsentId(null);
                    digitalAddressRepository.save(digitalAddress);
                }
                return false;
            }
        }

        return passwordEncoder.matches(upiPin, consent.getUpiPinHash());
    }

    /**
     * Get consent by token (for AIU to validate)
     */
    public Optional<Consent> getConsentByToken(String token) {
        Optional<Consent> consentOpt = consentRepository.findByConsentTokenAndIsActiveTrue(token);
        
        if (consentOpt.isEmpty()) {
            return Optional.empty();
        }

        Consent consent = consentOpt.get();

        // Check expiry for temporary consents
        if (consent.getConsentType() == Consent.ConsentType.TEMPORARY) {
            if (consent.getExpiresAt() != null && consent.getExpiresAt().isBefore(LocalDateTime.now())) {
                // Deactivate expired consent
                consent.setActive(false);
                consentRepository.save(consent);
                return Optional.empty();
            }
        }

        return consentOpt;
    }

    /**
     * Revoke consent (deactivate)
     */
    @Transactional
    public void revokeConsent(Long digitalAddressId) {
        Optional<Consent> consentOpt = consentRepository.findByDigitalAddressIdAndIsActiveTrue(digitalAddressId);
        
        if (consentOpt.isPresent()) {
            Consent consent = consentOpt.get();
            consent.setActive(false);
            consentRepository.save(consent);

            // Update digital address
            DigitalAddress digitalAddress = digitalAddressRepository.findById(digitalAddressId).orElse(null);
            if (digitalAddress != null) {
                digitalAddress.setHasActiveConsent(false);
                digitalAddress.setActiveConsentId(null);
                digitalAddressRepository.save(digitalAddress);
            }
        }
    }

    /**
     * Update consent (create new one with new UPI PIN)
     */
    @Transactional
    public Consent updateConsent(Long userId, Long digitalAddressId, String newUpiPin, 
                                 Consent.ConsentType consentType, Integer durationDays) {
        // Revoke existing consent
        revokeConsent(digitalAddressId);
        
        // Create new consent
        return createConsent(userId, digitalAddressId, newUpiPin, consentType, durationDays);
    }

    /**
     * Get active consent for a digital address
     */
    public Optional<Consent> getActiveConsent(Long digitalAddressId) {
        return consentRepository.findByDigitalAddressIdAndIsActiveTrue(digitalAddressId);
    }
}
