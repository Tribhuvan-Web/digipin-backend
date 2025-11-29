package com.hackathon.resolutionconsent.digipin.service;

import com.hackathon.resolutionconsent.digipin.dto.CreateDigitalAddressRequest;
import com.hackathon.resolutionconsent.digipin.dto.UpdateDigitalAddressRequest;
import com.hackathon.resolutionconsent.digipin.models.Consent;
import com.hackathon.resolutionconsent.digipin.models.DigitalAddress;
import com.hackathon.resolutionconsent.digipin.models.User;
import com.hackathon.resolutionconsent.digipin.repository.DigitalAddressRepository;
import com.hackathon.resolutionconsent.digipin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DigitalAddressService {

    @Autowired
    private DigitalAddressRepository digitalAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsentService consentService;

    @Transactional
    public DigitalAddress createDigitalAddress(CreateDigitalAddressRequest request, String generatedDigipin) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fullDigitalAddress = request.getUsername() +"@"+ request.getSuffix();

        if (digitalAddressRepository.findByDigitalAddress(fullDigitalAddress).isPresent()) {
            throw new RuntimeException("Digital address already exists");
        }

        DigitalAddress digitalAddress = new DigitalAddress();
        digitalAddress.setUserId(user.getId());
        digitalAddress.setDigitalAddress(fullDigitalAddress);
        digitalAddress.setGeneratedDigipin(generatedDigipin);
        digitalAddress.setSuffix(request.getSuffix());
        digitalAddress.setLatitude(request.getLatitude());
        digitalAddress.setLongitude(request.getLongitude());
        digitalAddress.setAddress(request.getAddress());

        DigitalAddress savedAddress = digitalAddressRepository.save(digitalAddress);

        Consent.ConsentType consentType = Consent.ConsentType.valueOf(request.getConsentType());
        consentService.createConsent(
            user.getId(), 
            savedAddress.getId(), 
            request.getUpiPin(), 
            consentType,
            request.getConsentDurationDays()
        );

        return savedAddress;
    }

    public Optional<DigitalAddress> getDigitalAddressById(Long id) {
        return digitalAddressRepository.findById(id);
    }

    public Optional<DigitalAddress> getDigitalAddressByDigipin(String digipin) {
        return digitalAddressRepository.findByDigitalAddress(digipin);
    }

    public List<DigitalAddress> getDigitalAddressesByUserId(Long userId) {
        return digitalAddressRepository.findByUserId(userId);
    }

    @Transactional
    public DigitalAddress updateDigitalAddressByUserId(Long userId, UpdateDigitalAddressRequest request,
            String regeneratedDigipin) {
        List<DigitalAddress> userAddresses = digitalAddressRepository.findByUserId(userId);

        if (userAddresses.isEmpty()) {
            throw new RuntimeException("No digital address found for this user");
        }

          DigitalAddress digitalAddress = userAddresses.get(0);

        // Verify UPI PIN before allowing update
        if (!consentService.verifyUpiPin(digitalAddress.getId(), request.getUpiPin())) {
            throw new RuntimeException("Invalid UPI PIN");
        }

        if (request.getLatitude() != null) {
            digitalAddress.setLatitude(request.getLatitude());
            // Update the generated digipin when coordinates change
            if (regeneratedDigipin != null) {
                digitalAddress.setGeneratedDigipin(regeneratedDigipin);
            }
        }
        if (request.getLongitude() != null) {
            digitalAddress.setLongitude(request.getLongitude());
        }
        if (request.getAddress() != null) {
            digitalAddress.setAddress(request.getAddress());
        }

        DigitalAddress updatedAddress = digitalAddressRepository.save(digitalAddress);

        // Update consent
        Consent.ConsentType consentType = Consent.ConsentType.valueOf(request.getConsentType());
        consentService.updateConsent(
            userId, 
            digitalAddress.getId(), 
            request.getUpiPin(), 
            consentType,
            request.getConsentDurationDays()
        );

        return updatedAddress;
    }
}
