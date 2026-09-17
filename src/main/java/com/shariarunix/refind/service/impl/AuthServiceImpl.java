package com.shariarunix.refind.service.impl;

import com.shariarunix.refind.dto.auth.AuthResponse;
import com.shariarunix.refind.dto.auth.LoginRequest;
import com.shariarunix.refind.dto.auth.RefreshTokenRequest;
import com.shariarunix.refind.dto.auth.RegisterRequest;
import com.shariarunix.refind.dto.user.UserProfileResponse;
import com.shariarunix.refind.entity.User;
import com.shariarunix.refind.entity.enums.Role;
import com.shariarunix.refind.entity.enums.UserStatus;
import com.shariarunix.refind.exception.BadRequestException;
import com.shariarunix.refind.exception.UserAccountDisabledException;
import com.shariarunix.refind.repository.UserRepository;
import com.shariarunix.refind.security.CustomUserDetails;
import com.shariarunix.refind.security.JwtTokenProvider;
import com.shariarunix.refind.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Value("${app.admin.bootstrap-emails:}")
    private String bootstrapEmails;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = StringUtils.hasText(request.getEmail()) ? request.getEmail().trim().toLowerCase() : null;
        String phone = StringUtils.hasText(request.getPhone()) ? request.getPhone().trim() : null;

        if (!StringUtils.hasText(email) && !StringUtils.hasText(phone)) {
            throw new BadRequestException("At least one contact method (email or phone) is required for registration");
        }

        if (email != null && userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with this email already exists: " + email);
        }

        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new BadRequestException("An account with this phone number already exists: " + phone);
        }

        Role assignedRole = determineRoleForEmail(email);

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .phone(phone)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(assignedRole)
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);
        log.info("Registered new user with ID: {}, email: {}, role: {}", user.getId(), user.getEmail(), user.getRole());

        String username = email != null ? email : phone;
        String accessToken = tokenProvider.generateAccessToken(user.getId(), username, user.getRole().name());
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpirationMs() / 1000)
                .user(mapToUserProfile(user))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String identifier = request.getIdentifier().trim();
        if (identifier.contains("@")) {
            identifier = identifier.toLowerCase();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (userDetails.getStatus() == UserStatus.BLOCKED) {
            throw new UserAccountDisabledException("This account has been blocked. Please contact support.");
        }
        if (userDetails.getStatus() == UserStatus.INACTIVE) {
            throw new UserAccountDisabledException("This account is currently inactive.");
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BadRequestException("User record not found"));

        String username = userDetails.getUsername();
        String role = user.getRole().name();

        String accessToken = tokenProvider.generateAccessToken(user.getId(), username, role);
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpirationMs() / 1000)
                .user(mapToUserProfile(user))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        if (!tokenProvider.validateToken(token)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        Long userId = tokenProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found for token"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserAccountDisabledException("Account is not active");
        }

        String username = user.getEmail() != null ? user.getEmail() : user.getPhone();
        String newAccessToken = tokenProvider.generateAccessToken(user.getId(), username, user.getRole().name());
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpirationMs() / 1000)
                .user(mapToUserProfile(user))
                .build();
    }

    private Role determineRoleForEmail(String email) {
        if (email != null && StringUtils.hasText(bootstrapEmails)) {
            List<String> adminList = Arrays.stream(bootstrapEmails.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();
            if (adminList.contains(email.toLowerCase())) {
                return Role.ROLE_ADMIN;
            }
        }
        return Role.ROLE_USER;
    }

    private UserProfileResponse mapToUserProfile(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
