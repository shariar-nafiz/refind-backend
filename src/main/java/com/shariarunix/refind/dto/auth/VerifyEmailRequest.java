package com.shariarunix.refind.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailRequest {

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    @Schema(description = "Registered email address awaiting verification", example = "user@example.com")
    private String email;

    @NotBlank(message = "OTP verification code is required")
    @Pattern(regexp = "^\\d{6}$", message = "Verification code must be exactly 6 digits")
    @Schema(description = "6-digit OTP received via email", example = "123456")
    private String otp;
}
