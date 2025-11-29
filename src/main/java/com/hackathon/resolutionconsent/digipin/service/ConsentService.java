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

@Service
public class ConsentService {

    @Autowired
    private ConsentRepository consentRepository;

    @Autowired
    private DigitalAddressRepository digitalAddressRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public Consent createConsent(Long userId, Long digitalAddressId, String uniPin, 
                                 Consent.ConsentType consentType, Integer durationDays) {
        
        if (uniPin == null || !uniPin.matches("^[0-9]{6}$")) {
            throw new IllegalArgumentException("UPI PIN must be exactly 6 digits");
        }

        Optional<Consent> existingConsent = consentRepository.findByDigitalAddressIdAndIsActiveTrue(digitalAddressId);
        existingConsent.ifPresent(consent -> {
            consent.setActive(false);
            consentRepository.save(consent);
        });

        Consent consent = new Consent();
        consent.setUserId(userId);
        consent.setDigitalAddressId(digitalAddressId);
        consent.setUpiPinHash(passwordEncoder.encode(uniPin));
        consent.setConsentType(consentType);
        consent.setConsentToken(generateConsentToken());

        if (consentType == Consent.ConsentType.TEMPORARY) {
            int days = (durationDays != null && durationDays > 0) ? durationDays : 30;
            consent.setExpiresAt(LocalDateTime.now().plusDays(days));
        }

        Consent savedConsent = consentRepository.save(consent);

        DigitalAddress digitalAddress = digitalAddressRepository.findById(digitalAddressId)
                .orElseThrow(() -> new RuntimeException("Digital address not found"));
        digitalAddress.setActiveConsentId(savedConsent.getId());
        digitalAddress.setHasActiveConsent(true);
        digitalAddressRepository.save(digitalAddress);

        return savedConsent;
    }

    public boolean verifyUpiPin(Long digitalAddressId, String uniPin) {
        Optional<Consent> consentOpt = consentRepository.findByDigitalAddressIdAndIsActiveTrue(digitalAddressId);
        
        if (consentOpt.isEmpty()) {
            return false;
        }

        Consent consent = consentOpt.get();

        if (consent.getConsentType() == Consent.ConsentType.TEMPORARY) {
            if (consent.getExpiresAt() != null && consent.getExpiresAt().isBefore(LocalDateTime.now())) {
                consent.setActive(false);
                consentRepository.save(consent);

                DigitalAddress digitalAddress = digitalAddressRepository.findById(digitalAddressId).orElse(null);
                if (digitalAddress != null) {
                    digitalAddress.setHasActiveConsent(false);
                    digitalAddress.setActiveConsentId(null);
                    digitalAddressRepository.save(digitalAddress);
                }
                return false;
            }
        }

        return passwordEncoder.matches(uniPin, consent.getUpiPinHash());
    }

    public Optional<Consent> getConsentByToken(String token) {
        Optional<Consent> consentOpt = consentRepository.findByConsentTokenAndIsActiveTrue(token);
        
        if (consentOpt.isEmpty()) {
            return Optional.empty();
        }

        Consent consent = consentOpt.get();

        if (consent.getConsentType() == Consent.ConsentType.TEMPORARY) {
            if (consent.getExpiresAt() != null && consent.getExpiresAt().isBefore(LocalDateTime.now())) {
                consent.setActive(false);
                consentRepository.save(consent);
                return Optional.empty();
            }
        }

        return consentOpt;
    }

    @Transactional
    public Consent updateConsent(Long userId, Long digitalAddressId, String newUpiPin, 
                                 Consent.ConsentType consentType, Integer durationDays) {
        Optional<Consent> consentOpt = consentRepository.findByDigitalAddressIdAndIsActiveTrue(digitalAddressId);
        
        if (consentOpt.isPresent()) {
            Consent consent = consentOpt.get();
            consent.setActive(false);
            consentRepository.save(consent);

            DigitalAddress digitalAddress = digitalAddressRepository.findById(digitalAddressId).orElse(null);
            if (digitalAddress != null) {
                digitalAddress.setHasActiveConsent(false);
                digitalAddress.setActiveConsentId(null);
                digitalAddressRepository.save(digitalAddress);
            }
        }
        
        return createConsent(userId, digitalAddressId, newUpiPin, consentType, durationDays);
    }

    private String generateConsentToken() {
        String chars = "0123456789"; // Excluding similar looking: 0,O,1,I
        StringBuilder token = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        do {
            token.setLength(0);
            for (int i = 0; i < 6; i++) {
                token.append(chars.charAt(random.nextInt(chars.length())));
            }
        } while (consentRepository.findByConsentTokenAndIsActiveTrue(token.toString()).isPresent());
        
        return token.toString();
    }

    public Optional<Consent> getActiveConsent(Long digitalAddressId) {
        return consentRepository.findByDigitalAddressIdAndIsActiveTrue(digitalAddressId);
    }
}
