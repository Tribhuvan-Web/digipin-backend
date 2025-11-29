package com.hackathon.resolutionconsent.digipin.controller;

import com.hackathon.resolutionconsent.digipin.dto.CreateDigitalAddressRequest;
import com.hackathon.resolutionconsent.digipin.dto.ResolveAddressWithConsentRequest;
import com.hackathon.resolutionconsent.digipin.dto.UpdateDigitalAddressRequest;
import com.hackathon.resolutionconsent.digipin.models.Consent;
import com.hackathon.resolutionconsent.digipin.models.DigitalAddress;
import com.hackathon.resolutionconsent.digipin.models.User;
import com.hackathon.resolutionconsent.digipin.service.AuthService;
import com.hackathon.resolutionconsent.digipin.service.ConsentService;
import com.hackathon.resolutionconsent.digipin.service.DigitalAddressService;
import com.hackathon.resolutionconsent.digipin.service.ImmuDBService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/digital-address")
@CrossOrigin(origins = "*")
public class DigitalAddressController {

    @Autowired
    private DigitalAddressService digitalAddressService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ConsentService consentService;

    @Autowired
    private ImmuDBService immuDBService;

    @PostMapping("/create")
    public ResponseEntity<?> createDigitalAddress(
            @Valid @RequestBody CreateDigitalAddressRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = authService.getUserFromToken(token);

            if (!user.isAadhaarVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("aadhaar not verified for this account");
            }

            request.setUserId(user.getId());
            request.setUsername(user.getUserName());

            String generatedDigipin = getDigiPin(request.getLatitude(), request.getLongitude());
            request.setDigipin(generatedDigipin);

            DigitalAddress createdAddress = digitalAddressService.createDigitalAddress(request, generatedDigipin);

             consentService.getActiveConsent(createdAddress.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body("Digital address created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("error while creating digital address: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDigitalAddressById(@PathVariable Long id) {
        return digitalAddressService.getDigitalAddressById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateDigitalAddress(
            @Valid @RequestBody UpdateDigitalAddressRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = authService.getUserFromToken(token);
            String regeneratedDigipin = getDigiPin(request.getLatitude(), request.getLongitude());
            digitalAddressService.updateDigitalAddressByUserId(user.getId(), request, regeneratedDigipin);
            return ResponseEntity.status(HttpStatus.OK).body("Digital address updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Invalid UPI PIN")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid UPI PIN");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("try again: " + e.getMessage());
        }
    }

    @PostMapping("/resolve-with-consent")
    public ResponseEntity<?> resolveAddressWithConsent(
            @Valid @RequestBody ResolveAddressWithConsentRequest request) {
        boolean resolutionSuccess = false;
        try {
            // Step 1: Find the digital address
            Optional<DigitalAddress> addressOpt = digitalAddressService.getDigitalAddressByDigipin(
                    request.getDigitalAddress());
            if (addressOpt.isEmpty()) {
                // Log failed resolution attempt to ImmuDB
                immuDBService.logAddressResolution(
                    request.getDigitalAddress(),
                    "N/A",
                    "ADDRESS_NOT_FOUND",
                    false
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Digital address not found");
            }
            DigitalAddress address = addressOpt.get();
            
            // Step 2: Verify UPI PIN - user must provide the unhashed PIN which was set during creation
            if (!consentService.verifyUpiPin(address.getId(), request.getUpiPin())) {
                // Log failed resolution attempt to ImmuDB
                immuDBService.logAddressResolution(
                    request.getDigitalAddress(),
                    "N/A",
                    "INVALID_UPI_PIN",
                    false
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid UPI PIN");
            }
            
            // Step 3: Fetch active consent in the background (automatically)
            Optional<Consent> consentOpt = consentService.getActiveConsent(address.getId());
            if (consentOpt.isEmpty()) {
                // Log failed resolution attempt to ImmuDB
                immuDBService.logAddressResolution(
                    request.getDigitalAddress(),
                    "N/A",
                    "NO_ACTIVE_CONSENT",
                    false
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("No active consent found for this digital address");
            }
            Consent consent = consentOpt.get();
            
            resolutionSuccess = true;
            
            // Log successful resolution to ImmuDB
            immuDBService.logAddressResolution(
                address.getDigitalAddress(),
                consent.getConsentToken(),
                "AIU_ACCESS",
                true
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("digitalAddress", address.getDigitalAddress());
            response.put("generatedDigipin", address.getGeneratedDigipin());
            response.put("latitude", address.getLatitude());
            response.put("longitude", address.getLongitude());
            response.put("address", address.getAddress());
            response.put("tamperProof", true);
            response.put("verifiedByImmuDB", true);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Log error to ImmuDB
            try {
                immuDBService.logAddressResolution(
                    request.getDigitalAddress(),
                    "N/A",
                    "ERROR: " + e.getMessage(),
                    false
                );
            } catch (Exception ignored) {}
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error resolving address: " + e.getMessage());
        }
    }

    private static final char[][] DIGIPIN_GRID = {
            { 'F', 'C', '9', '8' },
            { 'J', '3', '2', '7' },
            { 'K', '4', '5', '6' },
            { 'L', 'M', 'P', 'T' }
    };

    private static final double MIN_LAT = 2.5;
    private static final double MAX_LAT = 38.5;
    private static final double MIN_LON = 63.5;
    private static final double MAX_LON = 99.5;

    public static String getDigiPin(double lat, double lon) {
        if (lat < MIN_LAT || lat > MAX_LAT)
            throw new IllegalArgumentException("Latitude out of range");
        if (lon < MIN_LON || lon > MAX_LON)
            throw new IllegalArgumentException("Longitude out of range");

        double minLat = MIN_LAT;
        double maxLat = MAX_LAT;
        double minLon = MIN_LON;
        double maxLon = MAX_LON;

        StringBuilder digiPin = new StringBuilder();

        for (int level = 1; level <= 10; level++) {
            double latDiv = (maxLat - minLat) / 4.0;
            double lonDiv = (maxLon - minLon) / 4.0;

            int row = 3 - (int) Math.floor((lat - minLat) / latDiv);
            int col = (int) Math.floor((lon - minLon) / lonDiv);

            row = Math.max(0, Math.min(row, 3));
            col = Math.max(0, Math.min(col, 3));

            digiPin.append(DIGIPIN_GRID[row][col]);

            if (level == 3 || level == 6)
                digiPin.append('-');

            maxLat = minLat + latDiv * (4 - row);
            minLat = minLat + latDiv * (3 - row);

            minLon = minLon + lonDiv * col;
            maxLon = minLon + lonDiv;
        }

        return digiPin.toString();
    }

    /**
     * Get tamper-proof audit history for a digital address
     */
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

    /**
     * Get ImmuDB audit statistics
     */
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
}
