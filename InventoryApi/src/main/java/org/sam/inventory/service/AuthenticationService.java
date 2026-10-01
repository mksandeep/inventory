package org.sam.inventory.service;

import lombok.extern.slf4j.Slf4j;
import org.sam.inventory.dto.LoginRequest;
import org.sam.inventory.dto.LoginResponse;
import org.sam.inventory.dto.RegisterRequest;
import org.sam.inventory.entity.Role;
import org.sam.inventory.entity.User;
import org.sam.inventory.repository.RoleRepository;
import org.sam.inventory.repository.UserRepository;
import org.sam.inventory.security.EncryptionUtil;
import org.sam.inventory.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Decrypt password if encrypted flag is set
     */
    private String decryptPasswordIfNeeded(String password, Boolean encrypted) {
        if (encrypted != null && encrypted) {
            try {
                String decrypted = EncryptionUtil.decryptPasswordSimple(password);
                log.debug("Password decryption successful");
                return decrypted;
            } catch (Exception e) {
                log.error("Failed to decrypt password: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to decrypt password: " + e.getMessage());
            }
        }
        return password;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            log.info("Login attempt for email: {}", loginRequest.getEmail());

            // Decrypt password if it's encrypted
            String password = decryptPasswordIfNeeded(loginRequest.getPassword(), loginRequest.getEncrypted());
            log.debug("Password prepared for authentication");

            log.debug("Authenticating with AuthenticationManager...");
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), password);
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

            log.debug("Authentication successful");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = tokenProvider.generateToken(userDetails);
            String refreshToken = tokenProvider.generateRefreshToken(userDetails);

            User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();

            log.info("Login successful for email: {}", loginRequest.getEmail());
            return LoginResponse.builder().token(token).refreshToken(refreshToken).email(user.getEmail()).fullName(user.getFullName()).roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList())).build();
        } catch (Exception e) {
            log.error("Login failed for email: {}, error: {}", loginRequest.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Invalid email or password");
        }
    }

    public LoginResponse register(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Decrypt password if it's encrypted
        String password = decryptPasswordIfNeeded(registerRequest.getPassword(), registerRequest.getEncrypted());

        // Create new user
        User user = User.builder().email(registerRequest.getEmail()).password(passwordEncoder.encode(password)).fullName(registerRequest.getFullName()).active(true).roles(new HashSet<>()).build();

        // Assign roles
        if (registerRequest.getRoles() != null && !registerRequest.getRoles().isEmpty()) {
            for (String roleName : registerRequest.getRoles()) {
                Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                user.getRoles().add(role);
            }
        } else {
            // Default to USER role
            Role userRole = roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Default USER role not found"));
            user.getRoles().add(userRole);
        }

        User savedUser = userRepository.save(user);

        // Generate tokens
        String token = tokenProvider.generateToken(savedUser);
        String refreshToken = tokenProvider.generateRefreshToken(savedUser);

        return LoginResponse.builder().token(token).refreshToken(refreshToken).email(savedUser.getEmail()).fullName(savedUser.getFullName()).roles(savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toList())).build();
    }

    public LoginResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String email = tokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = tokenProvider.generateToken(user);

        return LoginResponse.builder().token(newAccessToken).refreshToken(refreshToken).email(user.getEmail()).fullName(user.getFullName()).roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList())).build();
    }
}



