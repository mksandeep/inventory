package org.sam.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.sam.inventory.dto.LoginRequest;
import org.sam.inventory.dto.LoginResponse;
import org.sam.inventory.entity.User;
import org.sam.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String LOGIN_URL = "/api/auth/login";
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @Test
    public void testSeededAdminPasswordMatchesConfiguredCredentials() {
        User adminUser = userRepository.findByEmail(ADMIN_EMAIL)
            .orElseThrow(() -> new AssertionError("Expected seeded admin user to exist: " + ADMIN_EMAIL));

        assertEquals(Boolean.TRUE, adminUser.getActive(), "Seeded admin user should be active");
        assertTrue(
            passwordEncoder.matches(ADMIN_PASSWORD, adminUser.getPassword()),
            "Stored BCrypt hash for admin@example.com must match admin123, otherwise AuthenticationManager will always return BadCredentials"
        );
    }

    @Test
    public void testLoginWithAdminCredentials() throws Exception {
        System.out.println("\n=== Testing Login with Admin Credentials ===");

        User adminUser = userRepository.findByEmail(ADMIN_EMAIL)
            .orElseThrow(() -> new AssertionError("Expected seeded admin user to exist: " + ADMIN_EMAIL));
        assertTrue(
            passwordEncoder.matches(ADMIN_PASSWORD, adminUser.getPassword()),
            "Precondition failed: seeded admin password hash does not match admin123"
        );

        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
            .email(ADMIN_EMAIL)
            .password(ADMIN_PASSWORD)
            .encrypted(false)
            .build();

        System.out.println("Request: " + objectMapper.writeValueAsString(loginRequest));

        // Act
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andReturn();

        // Print response
        int status = result.getResponse().getStatus();
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Response Status: " + status);
        System.out.println("Response Body: " + responseBody);

        // Assert
        if (status == 200) {
            LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
            assertNotNull(loginResponse);
            assertNotNull(loginResponse.getToken());
            assertEquals(ADMIN_EMAIL, loginResponse.getEmail());
            System.out.println("✓ Login successful!");
            System.out.println("  Email: " + loginResponse.getEmail());
            System.out.println("  Full Name: " + loginResponse.getFullName());
            System.out.println("  Roles: " + loginResponse.getRoles());
        } else if (status == 400) {
            System.out.println("✗ Login failed with 400 Bad Request");
            System.out.println("  Response: " + responseBody);
            fail("Login failed: " + responseBody);
        } else {
            fail("Unexpected status code: " + status + ", Response: " + responseBody);
        }
    }

    @Test
    public void testLoginWithInvalidPassword() throws Exception {
        System.out.println("\n=== Testing Login with Invalid Password ===");

        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
            .email(ADMIN_EMAIL)
            .password("wrongpassword")
            .encrypted(false)
            .build();

        // Act
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andReturn();

        // Print response
        int status = result.getResponse().getStatus();
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Response Status: " + status);
        System.out.println("Response Body: " + responseBody);

        // Assert
        assertEquals(400, status);
        assertTrue(responseBody.contains("Invalid") || responseBody.contains("invalid"));
        System.out.println("✓ Login properly rejected for invalid password");
    }

    @Test
    public void testLoginWithNonExistentEmail() throws Exception {
        System.out.println("\n=== Testing Login with Non-Existent Email ===");

        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
            .email("nonexistent@example.com")
            .password("anypassword")
            .encrypted(false)
            .build();

        // Act
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andReturn();

        // Print response
        int status = result.getResponse().getStatus();
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Response Status: " + status);
        System.out.println("Response Body: " + responseBody);

        // Assert
        assertEquals(400, status);
        assertTrue(responseBody.contains("Invalid") || responseBody.contains("invalid"));
        System.out.println("✓ Login properly rejected for non-existent email");
    }

    @Test
    public void testLoginWithEncryptedPassword() throws Exception {
        System.out.println("\n=== Testing Login with Encrypted Password ===");

        // Arrange - Create encrypted password with current timestamp
        long timestamp = System.currentTimeMillis();
        String plainText = ADMIN_PASSWORD + "|" + timestamp;
        String encodedPassword = java.util.Base64.getEncoder()
            .encodeToString(plainText.getBytes());

        LoginRequest loginRequest = LoginRequest.builder()
            .email(ADMIN_EMAIL)
            .password(encodedPassword)
            .encrypted(true)
            .build();

        System.out.println("Encrypted password (base64): " + encodedPassword.substring(0, Math.min(20, encodedPassword.length())) + "...");

        // Act
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andReturn();

        // Print response
        int status = result.getResponse().getStatus();
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Response Status: " + status);

        if (status != 200) {
            System.out.println("Response Body: " + responseBody);
        }

        // Assert
        if (status == 200) {
            LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
            assertNotNull(loginResponse);
            assertNotNull(loginResponse.getToken());
            assertEquals(ADMIN_EMAIL, loginResponse.getEmail());
            System.out.println("✓ Login with encrypted password successful!");
        } else {
            assertEquals(200, status, "Expected status 200 but got " + status + ": " + responseBody);
        }
    }
}

