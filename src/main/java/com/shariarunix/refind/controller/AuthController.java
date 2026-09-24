package com.shariarunix.refind.controller;

import com.shariarunix.refind.dto.auth.AuthResponse;
import com.shariarunix.refind.dto.auth.LoginRequest;
import com.shariarunix.refind.dto.auth.RefreshTokenRequest;
import com.shariarunix.refind.dto.auth.RegisterRequest;
import com.shariarunix.refind.dto.auth.RegisterResponse;
import com.shariarunix.refind.dto.auth.ResendOtpRequest;
import com.shariarunix.refind.dto.auth.VerifyEmailRequest;
import com.shariarunix.refind.dto.common.ApiResponse;
import com.shariarunix.refind.service.AuthService;
import com.shariarunix.refind.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, verification, login, and token refresh")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user account", description = "Creates a new user profile with email or phone number and dispatches verification OTP if registered with email.")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response.getMessage(), response, httpRequest.getRequestURI()));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email with 6-digit OTP", description = "Validates the 6-digit OTP code sent to user email, activates the account, and returns authentication tokens.")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request, HttpServletRequest httpRequest) {
        AuthResponse response = authService.verifyEmail(request);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", response, httpRequest.getRequestURI()));
    }

    @PostMapping("/resend-otp")
    @Operation(summary = "Resend verification OTP", description = "Sends a new 6-digit OTP to the user's registered email with rate-limit cooldown.")
    public ResponseEntity<ApiResponse<Void>> resendOtp(@Valid @RequestBody ResendOtpRequest request, HttpServletRequest httpRequest) {
        authService.resendOtp(request);
        return ResponseEntity.ok(ApiResponse.success("Verification code resent successfully. Please check your inbox.", httpRequest.getRequestURI()));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates user using email or phone and password, returning JWT access and refresh tokens.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response, httpRequest.getRequestURI()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT access token", description = "Generates a new access token using a valid refresh token.")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest httpRequest) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response, httpRequest.getRequestURI()));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout user",
            description = "Blacklists the current JWT access token and revokes the active refresh token in Redis.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletRequest httpRequest
    ) {
        String bearerToken = httpRequest.getHeader("Authorization");
        Long userId = userPrincipal != null ? userPrincipal.getId() : null;
        authService.logout(bearerToken, userId);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", httpRequest.getRequestURI()));
    }
}
