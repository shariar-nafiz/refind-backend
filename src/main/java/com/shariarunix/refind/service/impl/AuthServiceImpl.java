package com.shariarunix.refind.service.impl;

import com.shariarunix.refind.dto.auth.AuthResponse;
import com.shariarunix.refind.dto.auth.LoginRequest;
import com.shariarunix.refind.dto.auth.RefreshTokenRequest;
import com.shariarunix.refind.dto.auth.RegisterRequest;
import com.shariarunix.refind.dto.auth.RegisterResponse;
import com.shariarunix.refind.dto.auth.ResendOtpRequest;
import com.shariarunix.refind.dto.auth.VerifyEmailRequest;
import com.shariarunix.refind.dto.user.UserProfileResponse;
import com.shariarunix.refind.entity.User;
import com.shariarunix.refind.entity.enums.Role;
import com.shariarunix.refind.entity.enums.UserStatus;
import com.shariarunix.refind.exception.BadRequestException;
import com.shariarunix.refind.exception.UserAccountDisabledException;
import com.shariarunix.refind.repository.UserRepository;
import com.shariarunix.refind.security.UserPrincipal;
import com.shariarunix.refind.security.JwtTokenProvider;
import com.shariarunix.refind.service.AuthService;
import com.shariarunix.refind.service.EmailService;
import com.shariarunix.refind.service.OtpService;
import com.shariarunix.refind.service.RedisTokenService;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RedisTokenService redisTokenService;
    private final OtpService otpService;
    private final EmailService emailService;

    @Value("${app.admin.bootstrap-emails:}")
    private String bootstrapEmails;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = StringUtils.hasText(request.getEmail()) ? request.getEmail().trim().toLowerCase() : null;
        String phone = StringUtils.hasText(request.getPhone()) ? request.getPhone().trim() : null;

        if (!StringUtils.hasText(email) && !StringUtils.hasText(phone)) {
            throw new BadRequestException("At least one contact method (email or phone) is required for registration");
        }

        if (phone != null && userRepository.existsByPhone(phone)) {
            // Check if existing phone belongs to an active user
            User existingByPhone = userRepository.findByPhone(phone).orElse(null);
            if (existingByPhone != null && existingByPhone.getStatus() != UserStatus.PENDING) {
                throw new BadRequestException("An account with this phone number already exists: " + phone);
            }
        }

        Role assignedRole = determineRoleForEmail(email);

        if (StringUtils.hasText(email)) {
            Optional<User> existingUserOpt = userRepository.findByEmail(email);
            User user;
            if (existingUserOpt.isPresent()) {
                User existing = existingUserOpt.get();
                if (existing.getStatus() == UserStatus.PENDING) {
                    existing.setFullName(request.getFullName().trim());
                    existing.setPhone(phone);
                    existing.setPasswordHash(passwordEncoder.encode(request.getPassword()));
                    existing.setRole(assignedRole);
                    user = userRepository.save(existing);
                } else {
                    throw new BadRequestException("An account with this email already exists: " + email);
                }
            } else {
                user = User.builder()
                        .fullName(request.getFullName().trim())
                        .email(email)
                        .phone(phone)
                        .passwordHash(passwordEncoder.encode(request.getPassword()))
                        .role(assignedRole)
                        .status(UserStatus.PENDING)
                        .build();
                user = userRepository.save(user);
            }

            String otp = otpService.generateOtp(email);
            emailService.sendOtpEmail(email, user.getFullName(), otp);
            log.info("Registered user ID: {}, dispatched OTP to {}", user.getId(), user.getEmail());

            return RegisterResponse.builder()
                    .message("Registration successful. Please enter the 6-digit verification code sent to your email.")
                    .email(email)
                    .requiresVerification(true)
                    .build();
        } else {
            // User registered solely via phone (no email provided)
            User user = User.builder()
                    .fullName(request.getFullName().trim())
                    .phone(phone)
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(assignedRole)
                    .status(UserStatus.ACTIVE)
                    .build();
            user = userRepository.save(user);
            log.info("Registered new user with phone ID: {}, phone: {}", user.getId(), phone);

            return RegisterResponse.builder()
                    .message("Registration successful. You can now sign in.")
                    .email(phone)
                    .requiresVerification(false)
                    .build();
        }
    }

    @Override
    @Transactional
    public AuthResponse verifyEmail(VerifyEmailRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        otpService.verifyOtp(email, request.getOtp());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No account found associated with email: " + email));

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UserAccountDisabledException("This account has been blocked. Please contact support.");
        }

        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);
        log.info("User ID: {} successfully verified email and activated account", user.getId());

        String username = user.getEmail() != null ? user.getEmail() : user.getPhone();
        String accessToken = tokenProvider.generateAccessToken(user.getId(), username, user.getRole().name());
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        redisTokenService.storeRefreshToken(user.getId(), refreshToken, tokenProvider.getRefreshTokenExpirationMs());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpirationMs() / 1000)
                .user(mapToUserProfile(user))
                .build();
    }

    @Override
    public void resendOtp(ResendOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No account found associated with email: " + email));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new BadRequestException("This account is already verified. Please sign in.");
        }
        if (user.getStatus() != UserStatus.PENDING) {
            throw new UserAccountDisabledException("This account is not eligible for verification.");
        }

        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, user.getFullName(), otp);
        log.info("Successfully resent verification OTP to {}", email);
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

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

        if (userDetails.getStatus() == UserStatus.BLOCKED) {
            throw new UserAccountDisabledException("This account has been blocked. Please contact support.");
        }
        if (userDetails.getStatus() == UserStatus.INACTIVE) {
            throw new UserAccountDisabledException("This account is currently inactive.");
        }
        if (userDetails.getStatus() == UserStatus.PENDING) {
            throw new UserAccountDisabledException("This account is pending email verification. Please verify your email before logging in.");
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BadRequestException("User record not found"));

        String username = userDetails.getUsername();
        String role = user.getRole().name();

        String accessToken = tokenProvider.generateAccessToken(user.getId(), username, role);
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        redisTokenService.storeRefreshToken(user.getId(), refreshToken, tokenProvider.getRefreshTokenExpirationMs());

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

        if (!redisTokenService.validateRefreshToken(userId, token)) {
            throw new BadRequestException("Refresh token has expired or been revoked. Please sign in again.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found for token"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserAccountDisabledException("Account is not active");
        }

        String username = user.getEmail() != null ? user.getEmail() : user.getPhone();
        String newAccessToken = tokenProvider.generateAccessToken(user.getId(), username, user.getRole().name());
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getId());

        redisTokenService.storeRefreshToken(user.getId(), newRefreshToken, tokenProvider.getRefreshTokenExpirationMs());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpirationMs() / 1000)
                .user(mapToUserProfile(user))
                .build();
    }

    @Override
    public void logout(String bearerToken, Long userId) {
        if (StringUtils.hasText(bearerToken)) {
            String token = bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : bearerToken;
            long remainingTtl = tokenProvider.getRemainingExpirationMs(token);
            redisTokenService.blacklistToken(token, remainingTtl);
        }
        if (userId != null) {
            redisTokenService.revokeRefreshToken(userId);
        }
        log.info("User ID: {} successfully logged out and tokens revoked", userId);
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
