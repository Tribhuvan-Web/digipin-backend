package com.hackathon.resolutionconsent.digipin.service;

import com.hackathon.resolutionconsent.digipin.dto.AadhaarVerificationRequest;
import com.hackathon.resolutionconsent.digipin.models.AadhaarMockData;
import com.hackathon.resolutionconsent.digipin.models.User;
import com.hackathon.resolutionconsent.digipin.repository.AadhaarMockDataRepository;
import com.hackathon.resolutionconsent.digipin.repository.UserRepository;
import com.hackathon.resolutionconsent.digipin.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AadhaarMockDataRepository aadhaarMockDataRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public User getUserFromToken(String token) {
        try {
            Long userId = jwtUtil.extractUserIdFromAuthToken(token);
            return userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public boolean validateAuthToken(String token) {
        return jwtUtil.isAuthTokenValid(token);
    }

    public ResponseEntity<?> verifyAadhaar(Long userId, AadhaarVerificationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isAadhaarVerified()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("aadhaar verified");
        }

        Optional<User> existingAadhaarUser = userRepository.findByAadhaarNumber(request.getAadhaarNumber());
        if (existingAadhaarUser.isPresent() && !existingAadhaarUser.get().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("aadhaar already verified by another");
        }

        Optional<AadhaarMockData> aadhaarDataOpt = aadhaarMockDataRepository
                .findByAadhaarNumber(request.getAadhaarNumber());
        if (aadhaarDataOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Please enter a valid aadhaar number");
        }

        AadhaarMockData aadhaarData = aadhaarDataOpt.get();

        LocalDate providedDob = LocalDate.parse(request.getDateOfBirth());
        if (!aadhaarData.getDateOfBirth().equals(providedDob)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incorrect DOB, please enter the valid dob");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append("*");
        }
        user.setAadhaarNumber(sb.append(request.getAadhaarNumber().substring(8, 12)).toString());
        user.setAadhaarVerified(true);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("aadhaar card verified , You can now  create your digital address");
    }

}
