package com.shariarunix.refind.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    @Schema(description = "Response status message", example = "Registration successful. Please check your email for the verification code.")
    private String message;

    @Schema(description = "Registered email address", example = "user@example.com")
    private String email;

    @Schema(description = "Whether email verification is required to activate the account", example = "true")
    private boolean requiresVerification;
}
