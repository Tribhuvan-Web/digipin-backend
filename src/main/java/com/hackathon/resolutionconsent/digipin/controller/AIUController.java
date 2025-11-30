package com.hackathon.resolutionconsent.digipin.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.resolutionconsent.digipin.dto.ResolveAddressWithConsentRequest;
import com.hackathon.resolutionconsent.digipin.dto.ServiceFulfillmentFeedbackRequest;
import com.hackathon.resolutionconsent.digipin.models.Consent;
import com.hackathon.resolutionconsent.digipin.models.DigitalAddress;
import com.hackathon.resolutionconsent.digipin.service.ConsentService;
import com.hackathon.resolutionconsent.digipin.service.DigitalAddressService;
import com.hackathon.resolutionconsent.digipin.service.ImmuDBService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/aiu")
public class AIUController {

        private static final double CONFIDENCE_THRESHOLD = 70.0;

        @Autowired
        private DigitalAddressService digitalAddressService;

        @Autowired
        private ImmuDBService immuDBService;

        @Autowired
        private ConsentService consentService;

        @PostMapping("/resolve-with-consent")
        public ResponseEntity<?> resolveAddressWithConsent(
                        @Valid @RequestBody ResolveAddressWithConsentRequest request) {
                try {
                        Optional<DigitalAddress> addressOpt = digitalAddressService.getDigitalAddressByDigipin(
                                        request.getDigitalAddress());
                        if (addressOpt.isEmpty()) {
                                // Log failed resolution attempt to ImmuDB
                                immuDBService.logAddressResolution(
                                                request.getDigitalAddress(),
                                                "N/A",
                                                "ADDRESS_NOT_FOUND",
                                                false);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body("Digital address not found");
                        }
                        DigitalAddress address = addressOpt.get();

                        if (!consentService.verifyUpiPin(address.getId(), request.getUpiPin())) {
                                immuDBService.logAddressResolution(
                                                request.getDigitalAddress(),
                                                "N/A",
                                                "INVALID_UPI_PIN",
                                                false);
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body("Invalid UPI PIN");
                        }

                        Optional<Consent> consentOpt = consentService.getActiveConsent(address.getId());
                        if (consentOpt.isEmpty()) {
                                immuDBService.logAddressResolution(
                                                request.getDigitalAddress(),
                                                "N/A",
                                                "NO_ACTIVE_CONSENT",
                                                false);
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body("No active consent found for this digital address");
                        }
                        Consent consent = consentOpt.get();

                        immuDBService.logAddressResolution(
                                        address.getDigitalAddress(),
                                        consent.getConsentToken(),
                                        "AIU_ACCESS",
                                        true);

                        Map<String, Object> response = new HashMap<>();
                        response.put("digitalAddress", address.getDigitalAddress());
                        response.put("generatedDigipin", address.getGeneratedDigipin());
                        response.put("latitude", address.getLatitude());
                        response.put("longitude", address.getLongitude());
                        response.put("address", address.getAddress());
                        response.put("confidenceScore", address.getConfidenceScore());
                        response.put("pincode", address.getPinCode());
                        response.put("verificationType", address.getVerificationType().toString());
                        return ResponseEntity.ok(response);

                } catch (Exception e) {
                        try {
                                immuDBService.logAddressResolution(
                                                request.getDigitalAddress(),
                                                "N/A",
                                                "ERROR: " + e.getMessage(),
                                                false);
                        } catch (Exception ignored) {
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("Error resolving address: " + e.getMessage());
                }
        }

        @PostMapping("/feedback")
        public ResponseEntity<?> submitServiceFulfillmentFeedback(
                        @Valid @RequestBody ServiceFulfillmentFeedbackRequest request) {
                try {
                        Optional<DigitalAddress> addressOpt = digitalAddressService.getDigitalAddressByDigipin(
                                        request.getDigitalAddress());
                        if (addressOpt.isEmpty()) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body("Digital address not found");
                        }

                        DigitalAddress address = addressOpt.get();
                        double oldScore = address.getConfidenceScore();

                        digitalAddressService.updateConfidenceScore(address, request.getFulfillmentStatus());

                        try {
                                immuDBService.logConfidenceScoreUpdate(
                                                address.getDigitalAddress(),
                                                request.getAiuIdentifier(),
                                                request.getFulfillmentStatus().toString(),
                                                oldScore,
                                                address.getConfidenceScore(),
                                                request.getComments());
                        } catch (Exception e) {
                                System.err.println("ImmuDB confidence update logging failed: " + e.getMessage());
                        }

                        Map<String, Object> response = new HashMap<>();
                        response.put("message", "Service fulfillment feedback submitted successfully");
                        response.put("digitalAddress", address.getDigitalAddress());
                        response.put("oldConfidenceScore", oldScore);
                        response.put("newConfidenceScore", address.getConfidenceScore());
                        response.put("fulfillmentStatus", request.getFulfillmentStatus());
                        response.put("aiuIdentifier", request.getAiuIdentifier());
                        response.put("totalFulfillments", address.getTotalFulfillments());
                        response.put("isTrusted", address.getConfidenceScore() >= CONFIDENCE_THRESHOLD);

                        return ResponseEntity.ok(response);

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("Error submitting feedback: " + e.getMessage());
                }
        }
}
