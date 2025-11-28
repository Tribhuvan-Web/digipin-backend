package com.hackathon.resolutionconsent.digipin.controller;

import com.hackathon.resolutionconsent.digipin.dto.CreateDigitalAddressRequest;
import com.hackathon.resolutionconsent.digipin.dto.UpdateDigitalAddressRequest;
import com.hackathon.resolutionconsent.digipin.models.User;
import com.hackathon.resolutionconsent.digipin.service.AuthService;
import com.hackathon.resolutionconsent.digipin.service.DigitalAddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/digital-address")
@CrossOrigin(origins = "*")
public class DigitalAddressController {

    @Autowired
    private DigitalAddressService digitalAddressService;

    @Autowired
    private AuthService authService;

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
            digitalAddressService.createDigitalAddress(request, generatedDigipin);
            return ResponseEntity.status(HttpStatus.CREATED).body("Digital address created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error while creating digital address");
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
            digitalAddressService.updateDigitalAddressByUserId(user.getId(), request,
                    regeneratedDigipin);
            return ResponseEntity.status(HttpStatus.OK).body("Digital address updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("try again ");
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
