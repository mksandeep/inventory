package org.sam.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private java.util.List<String> roles; // e.g., ["USER", "MANAGER"]

    // Flag indicating if password is encrypted (base64 encoded with timestamp)
    // If true, password will be decrypted before registration
    // Default is false for backward compatibility
    @Default
    private Boolean encrypted = false;
}



