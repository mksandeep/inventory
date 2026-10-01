package org.sam.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sam.inventory.dto.LoginRequest;
import org.sam.inventory.dto.LoginResponse;
import org.sam.inventory.entity.Role;
import org.sam.inventory.entity.User;
import org.sam.inventory.repository.RoleRepository;
import org.sam.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String LOGIN_URL = "/api/auth/login";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "testPass123";
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @BeforeEach
    public void setup() {
        // Clean up
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create roles
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setDescription("Administrator role");

        Role userRole = new Role();
        userRole.setName("USER");
        userRole.setDescription("User role");

        roleRepository.save(adminRole);
        roleRepository.save(userRole);

        // Create test user
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        User testUser = User.builder()
            .email(TEST_EMAIL)
            .password(passwordEncoder.encode(TEST_PASSWORD))
            .fullName("Test User")
            .active(true)
            .roles(userRoles)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        userRepository.save(testUser);

        // Create admin user
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminRoles.add(userRole);
        User adminUser = User.builder()
            .email(ADMIN_EMAIL)
            .password(passwordEncoder.encode(ADMIN_PASSWORD))
            .fullName("Administrator")
            .active(true)
            .roles(adminRoles)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        userRepository.save(adminUser);
    }

    @Test
    public void testLoginWithValidPlainCredentials() throws Exception {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
            .email(TEST_EMAIL)
            .password(TEST_PASSWORD)
            .encrypted(false)
            .build();

        // Act
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        String responseBody = result.getResponse().getContentAsString();
        assertNotNull(responseBody);

        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getToken());
        assertNotNull(loginResponse.getRefreshToken());
        assertEquals(TEST_EMAIL, loginResponse.getEmail());
        assertEquals("Test User", loginResponse.getFullName());
        assertTrue(loginResponse.getRoles().contains("USER"));

        System.out.println("✓ Login with valid plain credentials successful!");
        System.out.println("  Token: " + loginResponse.getToken().substring(0, 20) + "...");
    }

    @Test
    public void testLoginWithAdminCredentials() throws Exception {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
            .email(ADMIN_EMAIL)
            .password(ADMIN_PASSWORD)
            .encrypted(false)
            .build();

        // Act
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        String responseBody = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);

        assertNotNull(loginResponse);
        assertEquals(ADMIN_EMAIL, loginResponse.getEmail());
        assertEquals("Administrator", loginResponse.getFullName());
        assertTrue(loginResponse.getRoles().contains("ADMIN"));
        assertTrue(loginResponse.getRoles().contains("USER"));

        System.out.println("✓ Login with admin credentials successful!");
        System.out.println("  Roles: " + loginResponse.getRoles());
    }

    @Test
    public void testLoginWithInvalidPassword() throws Exception {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
            .email(TEST_EMAIL)
            .password("wrongPassword")
            .encrypted(false)
            .build();

        // Act & Assert
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Invalid email or password"));

        System.out.println("✓ Login with invalid password properly rejected!");
    }

    @Test
    public void testLoginWithNonExistentEmail() throws Exception {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
            .email("nonexistent@example.com")
            .password("anypassword")
            .encrypted(false)
            .build();

        // Act & Assert
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Invalid email or password"));

        System.out.println("✓ Login with non-existent email properly rejected!");
    }

    @Test
    public void testLoginWithMissingEmail() throws Exception {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
            .password(TEST_PASSWORD)
            .encrypted(false)
            .build();

        // Act & Assert
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Email is required") || responseBody.contains("email"));

        System.out.println("✓ Login with missing email properly rejected!");
        System.out.println("  Error: " + responseBody);
    }

    @Test
    public void testLoginWithMissingPassword() throws Exception {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
            .email(TEST_EMAIL)
            .build();

        // Act & Assert
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Password is required") || responseBody.contains("password"));

        System.out.println("✓ Login with missing password properly rejected!");
    }

    @Test
    public void testLoginWithShortPassword() throws Exception {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
            .email(TEST_EMAIL)
            .password("pass")
            .encrypted(false)
            .build();

        // Act & Assert
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Password must be at least 6 characters"));

        System.out.println("✓ Login with short password properly rejected!");
    }

    @Test
    public void testLoginWithEncryptedPassword() throws Exception {
        // Arrange - Create encrypted password with timestamp
        long timestamp = System.currentTimeMillis();
        String plainText = TEST_PASSWORD + "|" + timestamp;
        String encodedPassword = java.util.Base64.getEncoder()
            .encodeToString(plainText.getBytes());

        LoginRequest loginRequest = LoginRequest.builder()
            .email(TEST_EMAIL)
            .password(encodedPassword)
            .encrypted(true)
            .build();

        // Act
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        String responseBody = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);

        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getToken());
        assertEquals(TEST_EMAIL, loginResponse.getEmail());

        System.out.println("✓ Login with encrypted password successful!");
    }

    @Test
    public void testLoginWithExpiredEncryptedPassword() throws Exception {
        // Arrange - Create encrypted password with old timestamp (more than 5 minutes ago)
        long timestamp = System.currentTimeMillis() - (6 * 60 * 1000); // 6 minutes ago
        String plainText = TEST_PASSWORD + "|" + timestamp;
        String encodedPassword = java.util.Base64.getEncoder()
            .encodeToString(plainText.getBytes());

        LoginRequest loginRequest = LoginRequest.builder()
            .email(TEST_EMAIL)
            .password(encodedPassword)
            .encrypted(true)
            .build();

        // Act & Assert
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Request expired") || responseBody.contains("Invalid email or password"));

        System.out.println("✓ Login with expired encrypted password properly rejected!");
    }
}

