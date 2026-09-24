package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.auth.AuthResponse;
import com.shariarunix.refind.dto.auth.LoginRequest;
import com.shariarunix.refind.dto.auth.RefreshTokenRequest;
import com.shariarunix.refind.dto.auth.RegisterRequest;
import com.shariarunix.refind.dto.auth.RegisterResponse;
import com.shariarunix.refind.dto.auth.ResendOtpRequest;
import com.shariarunix.refind.dto.auth.VerifyEmailRequest;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    AuthResponse verifyEmail(VerifyEmailRequest request);

    void resendOtp(ResendOtpRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String bearerToken, Long userId);
}

