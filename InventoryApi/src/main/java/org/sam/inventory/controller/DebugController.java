package org.sam.inventory.controller;

import lombok.extern.slf4j.Slf4j;
import org.sam.inventory.entity.User;
import org.sam.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@Slf4j
public class DebugController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        log.info("Debug: Fetching all users from database");
        List<User> users = userRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", users.size());
        response.put("users", users.stream().map(u -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("email", u.getEmail());
            userMap.put("fullName", u.getFullName());
            userMap.put("active", u.getActive());
            userMap.put("passwordHash", u.getPassword());
            userMap.put("roles", u.getRoles().stream().map(r -> r.getName()).toList());
            return userMap;
        }).toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin-user")
    public ResponseEntity<?> getAdminUser() {
        log.info("Debug: Fetching admin user");

        var adminUser = userRepository.findByEmail("admin@example.com");

        Map<String, Object> response = new HashMap<>();

        if (adminUser.isPresent()) {
            User user = adminUser.get();
            response.put("found", true);
            response.put("email", user.getEmail());
            response.put("fullName", user.getFullName());
            response.put("active", user.getActive());
            response.put("passwordHash", user.getPassword());
            response.put("roles", user.getRoles().stream().map(r -> r.getName()).toList());

            // Test password matching
            String testPassword = "admin123";
            boolean matches = passwordEncoder.matches(testPassword, user.getPassword());
            response.put("passwordMatches", matches);

            // Test with migration hash
            String migrationHash = "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5YmMxSUFutyal";
            boolean migrationMatches = passwordEncoder.matches(testPassword, migrationHash);
            response.put("migrationHashMatches", migrationMatches);
            response.put("migrationHash", migrationHash);
        } else {
            response.put("found", false);
            response.put("message", "Admin user not found in database");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-password")
    public ResponseEntity<?> testPassword() {
        log.info("Debug: Testing password encoding");

        String password = "admin123";
        String hash1 = passwordEncoder.encode(password);
        String hash2 = passwordEncoder.encode(password);

        Map<String, Object> response = new HashMap<>();
        response.put("password", password);
        response.put("encodedHash1", hash1);
        response.put("encodedHash2", hash2);
        response.put("migrationHash", "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5YmMxSUFutyal");
        response.put("testHashMatches", passwordEncoder.matches(password, hash1));
        response.put("migrationHashMatches", passwordEncoder.matches(password, "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5YmMxSUFutyal"));

        return ResponseEntity.ok(response);
    }
}

