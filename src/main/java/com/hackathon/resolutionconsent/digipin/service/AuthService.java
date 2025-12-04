package com.hackathon.resolutionconsent.digipin.service;

import com.hackathon.resolutionconsent.digipin.dto.AadhaarVerificationRequest;
import com.hackathon.resolutionconsent.digipin.dto.ForgotPasswordRequest;
import com.hackathon.resolutionconsent.digipin.models.AadhaarMockData;
import com.hackathon.resolutionconsent.digipin.models.User;
import com.hackathon.resolutionconsent.digipin.repository.AadhaarMockDataRepository;
import com.hackathon.resolutionconsent.digipin.repository.UserRepository;
import com.hackathon.resolutionconsent.digipin.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("New password and confirm password do not match");
        }

        Optional<User> userOpt = userRepository.findByEmailId(request.getEmailOrPhone());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByPhoneNumber(request.getEmailOrPhone());
        }

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No account found with this email or phone number");
        }

        User user = userOpt.get();

        if (!user.isAadhaarVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Aadhaar verification is required before password reset. Please verify your Aadhaar first.");
        }

        String maskedAadhaar = "********" + request.getAadhaarNumber().substring(8, 12);
        if (!user.getAadhaarNumber().equals(maskedAadhaar)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Aadhaar number does not match the registered account");
        }

        Optional<AadhaarMockData> aadhaarDataOpt = aadhaarMockDataRepository
                .findByAadhaarNumber(request.getAadhaarNumber());
        
        if (aadhaarDataOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Invalid Aadhaar number");
        }

        AadhaarMockData aadhaarData = aadhaarDataOpt.get();
        LocalDate providedDob = LocalDate.parse(request.getDateOfBirth());
        
        if (!aadhaarData.getDateOfBirth().equals(providedDob)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Date of birth does not match Aadhaar records");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK)
                .body("Password reset successful. You can now login with your new password.");
    }

}
