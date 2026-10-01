package org.sam.inventory.controller;

import org.junit.jupiter.api.Test;
import org.sam.inventory.entity.User;
import org.sam.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class DebugUserDataTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void debugUserData() {
        System.out.println("\n=== Debug User Data ===\n");

        // Check if admin user exists
        var adminUser = userRepository.findByEmail("admin@example.com");

        if (adminUser.isPresent()) {
            User user = adminUser.get();
            System.out.println("Admin user found!");
            System.out.println("  Email: " + user.getEmail());
            System.out.println("  Full Name: " + user.getFullName());
            System.out.println("  Active: " + user.getActive());
            System.out.println("  Password Hash: " + user.getPassword());
            System.out.println("  Roles: " + user.getRoles());
            System.out.println();

            // Test password matching
            String testPassword = "admin123";
            boolean matches = passwordEncoder.matches(testPassword, user.getPassword());
            System.out.println("Does 'admin123' match the hash? " + matches);
            System.out.println();

            // Generate a new hash for comparison
            String newHash = passwordEncoder.encode(testPassword);
            System.out.println("New hash for 'admin123': " + newHash);
            System.out.println();

            // Check the migration hash
            String migrationHash = "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5YmMxSUFutyal";
            boolean migrationMatches = passwordEncoder.matches(testPassword, migrationHash);
            System.out.println("Does 'admin123' match migration hash? " + migrationMatches);
            System.out.println("Migration hash: " + migrationHash);
        } else {
            System.out.println("Admin user NOT found!");
            System.out.println("All users in database:");
            userRepository.findAll().forEach(u -> {
                System.out.println("  - " + u.getEmail() + " (active: " + u.getActive() + ")");
            });
        }
    }
}

