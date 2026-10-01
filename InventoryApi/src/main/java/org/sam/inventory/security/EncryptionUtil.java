package org.sam.inventory.security;

import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

/**
 * Encryption utility for decrypting passwords sent from the client
 * Handles both simple base64 encoding and AES-GCM encryption
 */
@Slf4j
public class EncryptionUtil {

    /**
     * Decrypt password using simple base64 decoding
     * Extracts original password from encrypted format: password|timestamp
     */
    public static String decryptPasswordSimple(String encryptedPassword) throws Exception {
        try {
            if (encryptedPassword == null || encryptedPassword.trim().isEmpty()) {
                throw new IllegalArgumentException("Encrypted password cannot be empty");
            }

            log.debug("Starting password decryption...");

            // Decode from base64
            String decoded = new String(Base64.getDecoder().decode(encryptedPassword));
            log.debug("Base64 decoded successfully");

            // Extract password from format: password|timestamp
            String[] parts = decoded.split("\\|");
            if (parts.length < 2) {
                log.error("Invalid encrypted password format. Expected 'password|timestamp', got: {}", decoded);
                throw new IllegalArgumentException("Invalid encrypted password format");
            }

            String password = parts[0];
            String timestamp = parts[1];
            log.debug("Extracted password and timestamp from decoded string");

            // Validate timestamp (ensure it's not too old - e.g., older than 5 minutes)
            long requestTime = Long.parseLong(timestamp);
            long currentTime = System.currentTimeMillis();
            long diff = currentTime - requestTime;

            log.debug("Request timestamp: {}, Current time: {}, Difference: {} ms", requestTime, currentTime, diff);

            // 5 minutes in milliseconds
            long maxAgeMs = 5 * 60 * 1000;
            if (diff > maxAgeMs) {
                log.warn("Request expired. Timestamp is older than 5 minutes. Diff: {} ms (max: {} ms)", diff, maxAgeMs);
                throw new SecurityException("Request expired. Timestamp is older than 5 minutes.");
            }

            log.debug("Password decryption successful");
            return password;
        } catch (IllegalArgumentException | SecurityException e) {
            log.error("Decryption validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to decrypt password: {}", e.getMessage(), e);
            throw new Exception("Failed to decrypt password: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypt AES-GCM encrypted password
     * Format: iv:key:encryptedPassword (all base64 encoded)
     */
    public static String decryptPasswordAES(String encryptedData) throws Exception {
        try {
            if (encryptedData == null || encryptedData.trim().isEmpty()) {
                throw new IllegalArgumentException("Encrypted data cannot be empty");
            }

            String[] parts = encryptedData.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid encrypted data format. Expected: iv:key:encryptedPassword");
            }

            // This is a placeholder for AES-GCM decryption
            // In a production environment, you would implement full AES-GCM decryption
            // For now, we'll use the simple approach as it's more practical
            throw new UnsupportedOperationException("AES-GCM decryption not yet implemented. Use simple encryption.");
        } catch (Exception e) {
            throw new Exception("Failed to decrypt password: " + e.getMessage(), e);
        }
    }

    /**
     * Determine encryption type and decrypt accordingly
     */
    public static String decrypt(String encryptedPassword, boolean isSimpleEncryption) throws Exception {
        if (isSimpleEncryption) {
            return decryptPasswordSimple(encryptedPassword);
        } else {
            return decryptPasswordAES(encryptedPassword);
        }
    }

    /**
     * Validate if password is encrypted (base64 encoded)
     */
    public static boolean isValidBase64(String input) {
        try {
            Base64.getDecoder().decode(input);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

