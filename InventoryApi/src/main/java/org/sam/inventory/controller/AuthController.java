 package org.sam.inventory.controller;

import org.sam.inventory.dto.LoginRequest;
import org.sam.inventory.dto.LoginResponse;
import org.sam.inventory.dto.RegisterRequest;
import org.sam.inventory.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Authentication", description = "Authentication endpoints for login, register, and token refresh")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "User Login",
               description = "Authenticate user with email and encrypted password. " +
                           "Password should be encrypted on client side and encrypted flag set to true.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "User Registration",
               description = "Register a new user with email, encrypted password, and full name. " +
                           "Password should be encrypted on client side and encrypted flag set to true.")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        LoginResponse response = authenticationService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Refresh access token using refresh token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        LoginResponse response = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/add-user")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add New User (Admin Only)",
               description = "Add a new user to the system. Only administrators can use this endpoint.")
    public ResponseEntity<LoginResponse> addUser(@Valid @RequestBody RegisterRequest registerRequest) {
        LoginResponse response = authenticationService.register(registerRequest);
        return ResponseEntity.ok(response);
    }
}

