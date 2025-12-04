package com.hackathon.resolutionconsent.digipin.controller;

import com.hackathon.resolutionconsent.digipin.dto.AadhaarVerificationRequest;
import com.hackathon.resolutionconsent.digipin.dto.ForgotPasswordRequest;
import com.hackathon.resolutionconsent.digipin.dto.LoginRequest;
import com.hackathon.resolutionconsent.digipin.dto.RegisterRequest;
import com.hackathon.resolutionconsent.digipin.dto.UserProfileDto;
import com.hackathon.resolutionconsent.digipin.models.User;
import com.hackathon.resolutionconsent.digipin.repository.UserRepository;
import com.hackathon.resolutionconsent.digipin.service.AuthService;
import com.hackathon.resolutionconsent.digipin.util.JwtUtil;
import com.hackathon.resolutionconsent.digipin.util.UserDetailsImplement;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            if (userRepository.findByUserName(request.getUserName()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username already taken");
            }

            if (request.getEmailId() != null && !request.getEmailId().trim().isEmpty()) {
                if (userRepository.findByEmailId(request.getEmailId()).isPresent()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Email already registered");
                }
            }

            if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Phone number already registered");
            }

            User user = new User();
            user.setUserName(request.getUserName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmailId(request.getEmailId());
            user.setPhoneNumber(request.getPhoneNumber());

            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registered successfully");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Registration failed: Duplicate entry detected");
        } catch (jakarta.validation.ConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Username must contain only alphanumeric characters and underscores");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByEmailId(request.getEmailOrPhone());
            if (userOpt.isEmpty()) {
                userOpt = userRepository.findByPhoneNumber(request.getEmailOrPhone());
            }

            if (userOpt.isEmpty()) {
                throw new RuntimeException("Invalid credentials");
            }

            User user = userOpt.get();

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }

            UserDetailsImplement userDetails = UserDetailsImplement.build(user);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(userDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/verify-aadhaar")
    public ResponseEntity<?> verifyAadhaar(
            @Valid @RequestBody AadhaarVerificationRequest request) {
        try {
            org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext()
                    .getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            UserDetailsImplement userDetails = (UserDetailsImplement) authentication.getPrincipal();
            Long userId = userDetails.getId();

            return authService.verifyAadhaar(userId, request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = authService.getUserFromToken(token);
            UserProfileDto userProfileDto = new UserProfileDto(user.getUserName(), user.getPhoneNumber(),
                    user.getEmailId(), user.isAadhaarVerified());
            return ResponseEntity.ok(userProfileDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            return authService.forgotPassword(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Password reset failed: " + e.getMessage());
        }
    }
}
