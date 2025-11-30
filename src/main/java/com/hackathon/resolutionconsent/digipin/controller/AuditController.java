package com.hackathon.resolutionconsent.digipin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.resolutionconsent.digipin.models.DigitalAddress;
import com.hackathon.resolutionconsent.digipin.models.User;
import com.hackathon.resolutionconsent.digipin.service.AuthService;
import com.hackathon.resolutionconsent.digipin.service.DigitalAddressService;
import com.hackathon.resolutionconsent.digipin.service.ImmuDBService;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private static final double CONFIDENCE_THRESHOLD = 70.0;

    @Autowired
    private ImmuDBService immuDBService;

    @Autowired
    private AuthService authService;

    @Autowired
    private DigitalAddressService digitalAddressService;

    @GetMapping("/audit-history/{digitalAddress}")
    public ResponseEntity<?> getAuditHistory(
            @PathVariable String digitalAddress,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = authService.getUserFromToken(token);

            // Verify ownership
            Optional<DigitalAddress> addressOpt = digitalAddressService.getDigitalAddressByDigipin(digitalAddress);
            if (addressOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Digital address not found");
            }

            DigitalAddress address = addressOpt.get();
            if (!address.getUserId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You don't have permission to view this audit history");
            }

            List<Map<String, Object>> auditHistory = immuDBService.getAddressAuditHistory(digitalAddress);

            Map<String, Object> response = new HashMap<>();
            response.put("digitalAddress", digitalAddress);
            response.put("auditHistory", auditHistory);
            response.put("totalEvents", auditHistory.size());
            response.put("tamperProof", true);
            response.put("cryptographicallyVerified", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving audit history: " + e.getMessage());
        }
    }

    /**
     * Verify integrity of a specific audit entry
     */
    @PostMapping("/verify-audit")
    public ResponseEntity<?> verifyAuditEntry(
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String auditKey = request.get("auditKey");
            if (auditKey == null || auditKey.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Audit key is required");
            }

            Map<String, Object> verifiedEntry = immuDBService.verifyAuditEntry(auditKey);

            Map<String, Object> response = new HashMap<>();
            response.put("verified", true);
            response.put("auditEntry", verifiedEntry);
            response.put("message", "Data integrity verified - No tampering detected");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("DATA TAMPERED")) {
                Map<String, Object> response = new HashMap<>();
                response.put("verified", false);
                response.put("tampered", true);
                response.put("message", "WARNING: Data tampering detected!");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error verifying audit entry: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error verifying audit entry: " + e.getMessage());
        }
    }

    @GetMapping("/audit-stats")
    public ResponseEntity<?> getAuditStatistics(@RequestHeader("Authorization") String authHeader) {
        try {

            Map<String, Object> stats = immuDBService.getAuditStatistics();
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving audit statistics: " + e.getMessage());
        }
    }

    @GetMapping("/confidence-score/{digitalAddress}")
    public ResponseEntity<?> getConfidenceScore(@PathVariable String digitalAddress) {
        try {
            Optional<DigitalAddress> addressOpt = digitalAddressService.getDigitalAddressByDigipin(digitalAddress);
            if (addressOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Digital address not found");
            }

            DigitalAddress address = addressOpt.get();

            Map<String, Object> response = new HashMap<>();
            response.put("digitalAddress", address.getDigitalAddress());
            response.put("confidenceScore", address.getConfidenceScore());
            response.put("totalRatings", address.getTotalRatings());
            response.put("averageRating", address.getAverageRating());
            response.put("totalFulfillments", address.getTotalFulfillments());
            response.put("threshold", CONFIDENCE_THRESHOLD);
            response.put("isTrusted", address.getConfidenceScore() >= CONFIDENCE_THRESHOLD);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving confidence score: " + e.getMessage());
        }
    }

}
