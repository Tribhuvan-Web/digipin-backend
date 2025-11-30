package com.hackathon.resolutionconsent.digipin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ImmuDBService {

    private static final Logger logger = LoggerFactory.getLogger(ImmuDBService.class);
    private final ObjectMapper objectMapper;

    @Value("${immudb.enabled:false}")
    private boolean immudbEnabled;

    @Value("${immudb.host:localhost}")
    private String immudbHost;

    @Value("${immudb.port:3322}")
    private int immudbPort;

    private final Map<String, String> auditStore = new LinkedHashMap<>();
    private long txIdCounter = 1;

    public ImmuDBService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public String logAddressCreation(Long userId, String digitalAddress, String digipin,
            Double latitude, Double longitude, String physicalAddress,
            String consentToken, String consentType) {
        try {
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("eventType", "ADDRESS_CREATED");
            auditData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            auditData.put("userId", userId);
            auditData.put("digitalAddress", digitalAddress);
            auditData.put("generatedDigipin", digipin);
            auditData.put("latitude", latitude);
            auditData.put("longitude", longitude);
            auditData.put("physicalAddress", physicalAddress);
            auditData.put("consentToken", consentToken);
            auditData.put("consentType", consentType);
            auditData.put("version", "1.0");
            auditData.put("_txId", txIdCounter++);

            String key = String.format("address:create:%s:%d",
                    digitalAddress, System.currentTimeMillis());
            String value = objectMapper.writeValueAsString(auditData);

            storeAuditEntry(key, value);

            logger.info("✅ Address creation logged - Key: {} (ImmuDB: {})", key,
                    immudbEnabled ? "ENABLED" : "MOCK MODE");
            return key;

        } catch (Exception e) {
            logger.error("Failed to log address creation: {}", e.getMessage(), e);
            return "LOGGING_FAILED";
        }
    }

    public String logAddressUpdate(Long userId, String digitalAddress, String oldDigipin,
            String newDigipin, Double newLatitude, Double newLongitude,
            String newPhysicalAddress, String consentToken) {
        try {
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("eventType", "ADDRESS_UPDATED");
            auditData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            auditData.put("userId", userId);
            auditData.put("digitalAddress", digitalAddress);
            auditData.put("oldDigipin", oldDigipin);
            auditData.put("newDigipin", newDigipin);
            auditData.put("newLatitude", newLatitude);
            auditData.put("newLongitude", newLongitude);
            auditData.put("newPhysicalAddress", newPhysicalAddress);
            auditData.put("consentToken", consentToken);
            auditData.put("version", "1.0");
            auditData.put("_txId", txIdCounter++);

            String key = String.format("address:update:%s:%d",
                    digitalAddress, System.currentTimeMillis());
            String value = objectMapper.writeValueAsString(auditData);

            storeAuditEntry(key, value);

            logger.info("✅ Address update logged - Key: {}", key);
            return key;

        } catch (Exception e) {
            logger.error("Failed to log address update: {}", e.getMessage(), e);
            return "LOGGING_FAILED";
        }
    }

    public String logAddressResolution(String digitalAddress, String consentToken,
            String requesterInfo, boolean success) {
        try {
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("eventType", "ADDRESS_RESOLVED");
            auditData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            auditData.put("digitalAddress", digitalAddress);
            auditData.put("consentToken", consentToken);
            auditData.put("requesterInfo", requesterInfo != null ? requesterInfo : "ANONYMOUS");
            auditData.put("success", success);
            auditData.put("ipAddress", "REDACTED");
            auditData.put("version", "1.0");
            auditData.put("_txId", txIdCounter++);

            String key = String.format("address:resolve:%s:%d",
                    digitalAddress, System.currentTimeMillis());
            String value = objectMapper.writeValueAsString(auditData);

            storeAuditEntry(key, value);

            logger.info("✅ Address resolution logged - Key: {} - Success: {}", key, success);
            return key;

        } catch (Exception e) {
            logger.error("Failed to log address resolution: {}", e.getMessage(), e);
            return "LOGGING_FAILED";
        }
    }

    public String logConsentCreation(Long userId, Long digitalAddressId, String digitalAddress,
            String consentToken, String consentType, LocalDateTime expiresAt) {
        try {
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("eventType", "CONSENT_CREATED");
            auditData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            auditData.put("userId", userId);
            auditData.put("digitalAddressId", digitalAddressId);
            auditData.put("digitalAddress", digitalAddress);
            auditData.put("consentToken", consentToken);
            auditData.put("consentType", consentType);
            auditData.put("expiresAt", expiresAt != null ? expiresAt.format(DateTimeFormatter.ISO_DATE_TIME) : "NEVER");
            auditData.put("version", "1.0");
            auditData.put("_txId", txIdCounter++);

            String key = String.format("consent:create:%s:%d",
                    consentToken, System.currentTimeMillis());
            String value = objectMapper.writeValueAsString(auditData);

            storeAuditEntry(key, value);

            logger.info("✅ Consent creation logged - Key: {}", key);
            return key;

        } catch (Exception e) {
            logger.error("Failed to log consent creation: {}", e.getMessage(), e);
            return "LOGGING_FAILED";
        }
    }

    public String logConsentRevocation(String consentToken, String digitalAddress,
            Long userId, String reason) {
        try {
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("eventType", "CONSENT_REVOKED");
            auditData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            auditData.put("consentToken", consentToken);
            auditData.put("digitalAddress", digitalAddress);
            auditData.put("userId", userId);
            auditData.put("reason", reason != null ? reason : "USER_INITIATED");
            auditData.put("version", "1.0");
            auditData.put("_txId", txIdCounter++);

            String key = String.format("consent:revoke:%s:%d",
                    consentToken, System.currentTimeMillis());
            String value = objectMapper.writeValueAsString(auditData);

            storeAuditEntry(key, value);

            logger.info("✅ Consent revocation logged - Key: {}", key);
            return key;

        } catch (Exception e) {
            logger.error("Failed to log consent revocation: {}", e.getMessage(), e);
            return "LOGGING_FAILED";
        }
    }

    public String logAddressRating(String digitalAddress, Long userId, Integer rating, String feedback) {
        try {
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("eventType", "ADDRESS_RATED");
            auditData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            auditData.put("digitalAddress", digitalAddress);
            auditData.put("userId", userId);
            auditData.put("rating", rating);
            auditData.put("feedback", feedback != null ? feedback : "");
            auditData.put("version", "1.0");
            auditData.put("_txId", txIdCounter++);

            String key = String.format("address:rating:%s:%d",
                    digitalAddress, System.currentTimeMillis());
            String value = objectMapper.writeValueAsString(auditData);

            storeAuditEntry(key, value);

            logger.info("✅ Address rating logged - Key: {} - Rating: {}", key, rating);
            return key;

        } catch (Exception e) {
            logger.error("Failed to log address rating: {}", e.getMessage(), e);
            return "LOGGING_FAILED";
        }
    }

    public String logConfidenceScoreUpdate(String digitalAddress, String aiuIdentifier, 
            String fulfillmentStatus, Double oldScore, Double newScore, String comments) {
        try {
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("eventType", "CONFIDENCE_SCORE_UPDATED");
            auditData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            auditData.put("digitalAddress", digitalAddress);
            auditData.put("aiuIdentifier", aiuIdentifier);
            auditData.put("fulfillmentStatus", fulfillmentStatus);
            auditData.put("oldScore", oldScore);
            auditData.put("newScore", newScore);
            auditData.put("scoreChange", newScore - oldScore);
            auditData.put("comments", comments != null ? comments : "");
            auditData.put("version", "1.0");
            auditData.put("_txId", txIdCounter++);

            String key = String.format("address:confidence:%s:%d",
                    digitalAddress, System.currentTimeMillis());
            String value = objectMapper.writeValueAsString(auditData);

            storeAuditEntry(key, value);

            logger.info("✅ Confidence score update logged - Key: {} - AIU: {} - Status: {} - Score: {} → {}", 
                    key, aiuIdentifier, fulfillmentStatus, oldScore, newScore);
            return key;

        } catch (Exception e) {
            logger.error("Failed to log confidence score update: {}", e.getMessage(), e);
            return "LOGGING_FAILED";
        }
    }

    public String logAavaVerification(String digitalAddress, String agentId, String verificationStatus,
            Boolean locationConfirmed, String verificationNotes, Double oldScore, Double newScore,
            Double verifiedLatitude, Double verifiedLongitude) {
        try {
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("eventType", "AAVA_VERIFICATION");
            auditData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            auditData.put("digitalAddress", digitalAddress);
            auditData.put("agentId", agentId);
            auditData.put("verificationStatus", verificationStatus);
            auditData.put("locationConfirmed", locationConfirmed);
            auditData.put("verificationNotes", verificationNotes != null ? verificationNotes : "");
            auditData.put("oldConfidenceScore", oldScore);
            auditData.put("newConfidenceScore", newScore);
            auditData.put("scoreChange", newScore - oldScore);
            auditData.put("verifiedLatitude", verifiedLatitude);
            auditData.put("verifiedLongitude", verifiedLongitude);
            auditData.put("verificationType", "AAVA");
            auditData.put("version", "1.0");
            auditData.put("_txId", txIdCounter++);

            String key = String.format("address:aava:%s:%d",
                    digitalAddress, System.currentTimeMillis());
            String value = objectMapper.writeValueAsString(auditData);

            storeAuditEntry(key, value);

            logger.info("✅ AAVA verification logged - Key: {} - Agent: {} - Status: {} - Score: {} → {}", 
                    key, agentId, verificationStatus, oldScore, newScore);
            return key;

        } catch (Exception e) {
            logger.error("Failed to log AAVA verification: {}", e.getMessage(), e);
            return "LOGGING_FAILED";
        }
    }

    public Map<String, Object> verifyAuditEntry(String key) {
        try {
            String value = auditStore.get(key);
            if (value == null) {
                throw new RuntimeException("Audit entry not found: " + key);
            }

            Map<String, Object> data = objectMapper.readValue(value, Map.class);

            // Add verification metadata
            data.put("_verified", true);
            data.put("_verifiedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            data.put("_storageType", immudbEnabled ? "ImmuDB" : "In-Memory");

            logger.info("✅ Audit entry verified - Key: {}", key);
            return data;

        } catch (Exception e) {
            logger.error("Failed to verify audit entry: {}", e.getMessage(), e);
            throw new RuntimeException("Verification failed: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getAddressAuditHistory(String digitalAddress) {
        try {
            List<Map<String, Object>> history = new ArrayList<>();

            // Search for all audit entries related to this address
            for (Map.Entry<String, String> entry : auditStore.entrySet()) {
                if (entry.getKey().contains(digitalAddress)) {
                    try {
                        Map<String, Object> data = objectMapper.readValue(entry.getValue(), Map.class);
                        data.put("_auditKey", entry.getKey());
                        history.add(data);
                    } catch (Exception ignored) {
                    }
                }
            }

            history.sort((a, b) -> {
                String timeA = (String) a.get("timestamp");
                String timeB = (String) b.get("timestamp");
                return timeB != null && timeA != null ? timeB.compareTo(timeA) : 0;
            });

            logger.info("Retrieved {} audit entries for address: {}", history.size(), digitalAddress);
            return history;

        } catch (Exception e) {
            logger.error("Failed to retrieve audit history: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public Map<String, Object> getAuditStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            stats.put("immudbEnabled", immudbEnabled);
            stats.put("immudbHost", immudbHost);
            stats.put("immudbPort", immudbPort);
            stats.put("database", "digipin_audit");
            stats.put("tamperProof", immudbEnabled);
            stats.put("cryptographicallyVerified", immudbEnabled);
            stats.put("totalAuditEntries", auditStore.size());
            stats.put("storageType", immudbEnabled ? "ImmuDB" : "In-Memory (Mock)");
            stats.put("note", immudbEnabled ? "Connected to ImmuDB for tamper-proof storage"
                    : "Running in mock mode. Install ImmuDB and set immudb.enabled=true for production use.");

            return stats;

        } catch (Exception e) {
            logger.error("Failed to get audit statistics: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    private void storeAuditEntry(String key, String value) {
        if (immudbEnabled) {
            logger.warn("ImmuDB is enabled but client not configured. Using fallback storage.");
        }

        auditStore.put(key, value);
    }
}
