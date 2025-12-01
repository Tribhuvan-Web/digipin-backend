package com.hackathon.resolutionconsent.digipin.service;

import com.hackathon.resolutionconsent.digipin.dto.AavaVerificationRequest;
import com.hackathon.resolutionconsent.digipin.dto.CreateDigitalAddressRequest;
import com.hackathon.resolutionconsent.digipin.dto.ServiceFulfillmentFeedbackRequest;
import com.hackathon.resolutionconsent.digipin.dto.UpdateDigitalAddressRequest;
import com.hackathon.resolutionconsent.digipin.models.Consent;
import com.hackathon.resolutionconsent.digipin.models.DigitalAddress;
import com.hackathon.resolutionconsent.digipin.models.User;
import com.hackathon.resolutionconsent.digipin.repository.DigitalAddressRepository;
import com.hackathon.resolutionconsent.digipin.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Autowired
    private ImmuDBService immuDBService;

    @Transactional
    public DigitalAddress createDigitalAddress(CreateDigitalAddressRequest request, String generatedDigipin) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fullDigitalAddress = request.getUsername() + "@" + request.getSuffix();

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
        digitalAddress.setAddressName(request.getAddressName());
        digitalAddress.setPinCode(request.getPincode());
        digitalAddress.setPurpose(request.getPurpose());

        DigitalAddress savedAddress = digitalAddressRepository.save(digitalAddress);

        Consent.ConsentType consentType = Consent.ConsentType.valueOf(request.getConsentType());
        Consent consent = consentService.createConsent(
                user.getId(),
                savedAddress.getId(),
                request.getUniPin(),
                consentType,
                request.getConsentDurationDays());

        try {
            immuDBService.logAddressCreation(
                    user.getId(),
                    fullDigitalAddress,
                    generatedDigipin,
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getAddress(),
                    consent.getConsentToken(),
                    request.getConsentType());
        } catch (Exception e) {
            System.err.println("ImmuDB logging failed: " + e.getMessage());
        }

        return savedAddress;
    }

    public void deleteDigitalAddress(String digitalAddress, Long userId) {
        Optional<DigitalAddress> addressOpt = digitalAddressRepository.findByDigitalAddress(digitalAddress);

        if (addressOpt.isEmpty()) {
            throw new IllegalArgumentException("Digital address not found: " + digitalAddress);
        }

        DigitalAddress address = addressOpt.get();

        // Check if user owns this address
        if (!address.getUserId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own digital addresses");
        }

        // Check if address is AAVA verified
        if (address.getIsAavaVerified() != null && address.getIsAavaVerified()) {
            throw new IllegalStateException(
                    "Cannot delete AAVA verified address. AAVA verified addresses are protected and cannot be deleted.");
        }

        digitalAddressRepository.delete(address);
    }

    public Optional<DigitalAddress> getDigitalAddressByDigitaladdress(String digitalAddress) {
        return digitalAddressRepository.findByDigitalAddress(digitalAddress);
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

        if (!consentService.verifyUpiPin(digitalAddress.getId(), request.getUpiPin())) {
            throw new RuntimeException("Invalid UPI PIN");
        }

        String oldDigipin = digitalAddress.getGeneratedDigipin();

        if (request.getLatitude() != null) {
            digitalAddress.setLatitude(request.getLatitude());
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

        Consent.ConsentType consentType = Consent.ConsentType.valueOf(request.getConsentType());
        Consent newConsent = consentService.updateConsent(
                userId,
                digitalAddress.getId(),
                request.getUpiPin(),
                consentType,
                request.getConsentDurationDays());

        try {
            immuDBService.logAddressUpdate(
                    userId,
                    updatedAddress.getDigitalAddress(),
                    oldDigipin,
                    regeneratedDigipin != null ? regeneratedDigipin : oldDigipin,
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getAddress(),
                    newConsent.getConsentToken());
        } catch (Exception e) {
            System.err.println("ImmuDB logging failed: " + e.getMessage());
        }

        return updatedAddress;
    }

    @Transactional
    public void updateConfidenceScore(DigitalAddress address,
            ServiceFulfillmentFeedbackRequest.FulfillmentStatus status) {
        double currentScore = address.getConfidenceScore();
        double adjustment = 0.0;

        switch (status) {
            case SUCCESS:
                adjustment = 10.0;
                break;
            case FAILURE:
                adjustment = -15.0;
                break;
            case NEUTRAL:
                adjustment = -5.0;
                break;
        }

        double newScore = currentScore + adjustment;
        newScore = Math.max(0.0, Math.min(100.0, newScore));

        address.setConfidenceScore(Math.round(newScore * 100.0) / 100.0);
        address.setTotalFulfillments(address.getTotalFulfillments() + 1);

        digitalAddressRepository.save(address);
    }

    @Transactional
    public void processAavaVerification(DigitalAddress address, AavaVerificationRequest request) {
        if (request.getVerificationStatus() == AavaVerificationRequest.VerificationStatus.VERIFIED
                && request.getLocationConfirmed()) {

            address.setIsAavaVerified(true);
            address.setVerificationType(DigitalAddress.VerificationType.AAVA);
            address.setAavaAgentId(request.getAgentId());
            address.setAavaVerifiedAt(LocalDateTime.now());
            address.setAavaVerificationNotes(request.getVerificationNotes());
            address.setRequiresAavaVerification(false);

            double currentScore = address.getConfidenceScore();
            double boostedScore = Math.max(90.0, Math.min(95.0, currentScore + 40.0));
            address.setConfidenceScore(Math.round(boostedScore * 100.0) / 100.0);

        } else if (request.getVerificationStatus() == AavaVerificationRequest.VerificationStatus.VERIFICATION_FAILED) {
            address.setIsAavaVerified(false);
            address.setAavaAgentId(request.getAgentId());
            address.setAavaVerifiedAt(LocalDateTime.now());
            address.setAavaVerificationNotes(request.getVerificationNotes());

            address.setConfidenceScore(Math.max(0.0, address.getConfidenceScore() - 30.0));

        } else if (request.getVerificationStatus() == AavaVerificationRequest.VerificationStatus.REQUIRES_CORRECTION) {
            address.setAavaVerificationNotes(request.getVerificationNotes());
            address.setConfidenceScore(Math.max(0.0, address.getConfidenceScore() - 10.0));
        }

        digitalAddressRepository.save(address);
    }

    @Transactional
    public void flagForAavaVerification(DigitalAddress address, String reason) {
        address.setRequiresAavaVerification(true);
        digitalAddressRepository.save(address);
    }
}
