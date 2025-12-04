package com.hackathon.resolutionconsent.digipin.controller;

import com.hackathon.resolutionconsent.digipin.dto.CreateDigitalAddressRequest;
import com.hackathon.resolutionconsent.digipin.dto.DigitalAddressWithStatusDto;
import com.hackathon.resolutionconsent.digipin.models.Consent;
import com.hackathon.resolutionconsent.digipin.models.DigitalAddress;
import com.hackathon.resolutionconsent.digipin.models.User;
import com.hackathon.resolutionconsent.digipin.service.AuthService;
import com.hackathon.resolutionconsent.digipin.service.ConsentService;
import com.hackathon.resolutionconsent.digipin.service.DigitalAddressService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/digital-address")
public class DigitalAddressController {

    @Autowired
    private DigitalAddressService digitalAddressService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ConsentService consentService;

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

    @GetMapping("/{digitalAddress}")
    public ResponseEntity<?> getDigitalAddressById(
            @PathVariable String digitalAddress,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = authService.getUserFromToken(token);

            Optional<DigitalAddress> addressOpt = digitalAddressService
                    .getDigitalAddressByDigitaladdress(digitalAddress);

            if (addressOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Digital address not found");
            }

            DigitalAddress address = addressOpt.get();

            if (!address.getUserId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only view your own digital addresses");
            }

            return ResponseEntity.ok(address);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving digital address: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getDigitalAddresses(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        User user = authService.getUserFromToken(token);
        List<DigitalAddress> addresses = digitalAddressService.getDigitalAddressesByUserId(user.getId());
        
        List<DigitalAddressWithStatusDto> response = addresses.stream()
                .map(address -> buildAddressWithStatus(address))
                .toList();
        
        return ResponseEntity.ok(response);
    }
    
    private DigitalAddressWithStatusDto buildAddressWithStatus(DigitalAddress address) {
        Optional<Consent> consentOpt = consentService.getActiveConsent(address.getId());
        
        boolean isActive = false;
        boolean isExpired = true;
        String linkStatus = "EXPIRED";
        String consentType = "NONE";
        boolean isPermanent = false;
        boolean isTemporary = false;
        LocalDateTime expiresAt = null;
        Long daysRemaining = null;
        
        if (consentOpt.isPresent()) {
            Consent consent = consentOpt.get();
            consentType = consent.getConsentType().name();
            isPermanent = consent.getConsentType() == Consent.ConsentType.PERMANENT;
            isTemporary = consent.getConsentType() == Consent.ConsentType.TEMPORARY;
            expiresAt = consent.getExpiresAt();
            
            if (isPermanent) {
                isActive = true;
                isExpired = false;
                linkStatus = "ACTIVE";
            } else if (isTemporary) {
                // Check if temporary link has expired
                if (expiresAt != null && expiresAt.isAfter(LocalDateTime.now())) {
                    isActive = true;
                    isExpired = false;
                    linkStatus = "ACTIVE";
                    daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), expiresAt);
                } else {
                    isActive = false;
                    isExpired = true;
                    linkStatus = "EXPIRED";
                    daysRemaining = 0L;
                }
            }
        }
        
        return DigitalAddressWithStatusDto.builder()
                .id(address.getId())
                .digitalAddress(address.getDigitalAddress())
                .generatedDigipin(address.getGeneratedDigipin())
                .suffix(address.getSuffix())
                .addressName(address.getAddressName())
                .address(address.getAddress())
                .pinCode(address.getPinCode())
                .purpose(address.getPurpose())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .createdAt(address.getCreatedAt())
                .isActive(isActive)
                .isExpired(isExpired)
                .linkStatus(linkStatus)
                .consentType(consentType)
                .isPermanent(isPermanent)
                .isTemporary(isTemporary)
                .expiresAt(expiresAt)
                .daysRemaining(daysRemaining)
                .isAavaVerified(address.getIsAavaVerified())
                .confidenceScore(address.getConfidenceScore())
                .build();
    }

    @GetMapping("/digipin")
    public ResponseEntity<?> getDigipin(@RequestParam double lon,
            @RequestParam double lat) {
        return ResponseEntity.ok(getDigiPin(lat, lon));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteDigitalAddress(
            @RequestParam String digitalAddress,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = authService.getUserFromToken(token);
            digitalAddressService.deleteDigitalAddress(digitalAddress, user.getId());
            return ResponseEntity.ok("Digital address deleted successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting digital address: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateDigitalAddress(
            @Valid @RequestBody CreateDigitalAddressRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = authService.getUserFromToken(token);

            if (request.getDigitalAddress() == null || request.getDigitalAddress().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("digitalAddress field is required to specify which address to update");
            }

            request.setUserId(user.getId());
            request.setUsername(user.getUserName());

            String regeneratedDigipin = getDigiPin(request.getLatitude(), request.getLongitude());
            request.setDigipin(regeneratedDigipin);

            DigitalAddress updatedAddress = digitalAddressService.updateDigitalAddressByDigitalAddress(
                    user.getId(), request.getDigitalAddress(), request, regeneratedDigipin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Digital address updated successfully");
            response.put("digitalAddress", updatedAddress.getDigitalAddress());
            response.put("digipin", updatedAddress.getGeneratedDigipin());
            response.put("hasActiveConsent", updatedAddress.isHasActiveConsent());
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
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

    @PostMapping("/flag-for-aava")
    public ResponseEntity<?> flagForAavaVerification(
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String digitalAddress = request.get("digitalAddress");
            String reason = request.get("reason");

            if (digitalAddress == null || digitalAddress.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Digital address is required");
            }

            Optional<DigitalAddress> addressOpt = digitalAddressService.getDigitalAddressByDigipin(digitalAddress);
            if (addressOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Digital address not found");
            }

            DigitalAddress address = addressOpt.get();
            if (address.getIsAavaVerified()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Aava already verified for this account");
            }
            String token = authHeader.replace("Bearer ", "");
            User user = authService.getUserFromToken(token);

            if (!address.getUserId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only flag your own digital addresses");
            }

            digitalAddressService.flagForAavaVerification(address, reason);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Digital address flagged for AAVA verification");
            response.put("digitalAddress", address.getDigitalAddress());
            response.put("requiresAavaVerification", true);
            response.put("reason", reason);
            response.put("note", "An AAVA agent will be assigned for physical verification");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error flagging for AAVA verification: " + e.getMessage());
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

}
