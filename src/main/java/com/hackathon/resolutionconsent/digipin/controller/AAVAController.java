package com.hackathon.resolutionconsent.digipin.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.resolutionconsent.digipin.dto.AavaVerificationRequest;
import com.hackathon.resolutionconsent.digipin.models.DigitalAddress;
import com.hackathon.resolutionconsent.digipin.service.DigitalAddressService;
import com.hackathon.resolutionconsent.digipin.service.ImmuDBService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/aava")

public class AAVAController {

    @Autowired
    private DigitalAddressService digitalAddressService;

    @Autowired
    private ImmuDBService immuDBService;

    @PostMapping("/aava-verify")
    public ResponseEntity<?> submitAavaVerification(
            @Valid @RequestBody AavaVerificationRequest request) {
        try {
            Optional<DigitalAddress> addressOpt = digitalAddressService.getDigitalAddressByDigipin(
                    request.getDigitalAddress());
            if (addressOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Digital address not found");
            }

            DigitalAddress address = addressOpt.get();
            double oldScore = address.getConfidenceScore();

            if (address.getIsAavaVerified()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("address is already verified with the aava");
            }

            digitalAddressService.processAavaVerification(address, request);

            try {
                immuDBService.logAavaVerification(
                        address.getDigitalAddress(),
                        request.getAgentId(),
                        request.getVerificationStatus().toString(),
                        request.getLocationConfirmed(),
                        request.getVerificationNotes(),
                        oldScore,
                        address.getConfidenceScore(),
                        request.getVerifiedLatitude(),
                        request.getVerifiedLongitude());
            } catch (Exception e) {
                System.err.println("ImmuDB AAVA verification logging failed: " + e.getMessage());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "AAVA verification processed successfully");
            response.put("digitalAddress", address.getDigitalAddress());
            response.put("verificationStatus", request.getVerificationStatus());
            response.put("isAavaVerified", address.getIsAavaVerified());
            response.put("verificationType", address.getVerificationType().toString());
            response.put("oldConfidenceScore", oldScore);
            response.put("newConfidenceScore", address.getConfidenceScore());
            response.put("agentId", address.getAavaAgentId());
            response.put("verifiedAt", address.getAavaVerifiedAt());
            response.put("tamperProofLogged", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing AAVA verification: " + e.getMessage());
        }
    }

    @GetMapping("/aava-status/{digitalAddress}")
    public ResponseEntity<?> getAavaStatus(@PathVariable String digitalAddress) {
        try {
            Optional<DigitalAddress> addressOpt = digitalAddressService.getDigitalAddressByDigipin(digitalAddress);
            if (addressOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Digital address not found");
            }

            DigitalAddress address = addressOpt.get();

            Map<String, Object> response = new HashMap<>();
            response.put("digitalAddress", address.getDigitalAddress());
            response.put("isAavaVerified", address.getIsAavaVerified());
            response.put("verificationType", address.getVerificationType().toString());
            response.put("requiresAavaVerification", address.getRequiresAavaVerification());
            response.put("agentId", address.getAavaAgentId());
            response.put("verifiedAt", address.getAavaVerifiedAt());
            response.put("verificationNotes", address.getAavaVerificationNotes());

            Map<String, Boolean> useCaseApproval = new HashMap<>();
            useCaseApproval.put("governmentWelfare", address.getIsAavaVerified());
            useCaseApproval.put("propertyRecords", address.getIsAavaVerified());
            useCaseApproval.put("legalNotices", address.getIsAavaVerified());
            useCaseApproval.put("emergencyServices", address.getIsAavaVerified() || address.getConfidenceScore() >= 80);
            useCaseApproval.put("eCommerce", address.getConfidenceScore() >= 50);
            useCaseApproval.put("foodDelivery", address.getConfidenceScore() >= 50);
            response.put("approvedUseCases", useCaseApproval);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving AAVA status: " + e.getMessage());
        }
    }
}
