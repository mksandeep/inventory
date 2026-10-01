package org.sam.inventory;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "admin123";
        String hash1 = encoder.encode(password);
        String hash2 = encoder.encode(password);

        System.out.println("Password: " + password);
        System.out.println("Hash 1: " + hash1);
        System.out.println("Hash 2: " + hash2);
        System.out.println();
        System.out.println("Expected hash from migration:");
        System.out.println("$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5YmMxSUFutyal");
        System.out.println();

        // Test if the expected hash matches the password
        boolean matches1 = encoder.matches(password, "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5YmMxSUFutyal");
        System.out.println("Does '" + password + "' match the migration hash? " + matches1);
    }
}

