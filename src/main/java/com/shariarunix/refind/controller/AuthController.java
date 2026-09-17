package com.shariarunix.refind.controller;

import com.shariarunix.refind.dto.auth.AuthResponse;
import com.shariarunix.refind.dto.auth.LoginRequest;
import com.shariarunix.refind.dto.auth.RefreshTokenRequest;
import com.shariarunix.refind.dto.auth.RegisterRequest;
import com.shariarunix.refind.dto.common.ApiResponse;
import com.shariarunix.refind.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token refresh")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user account", description = "Creates a new user profile with email or phone number and returns authentication tokens.")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response, httpRequest.getRequestURI()));
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
}
