package com.shariarunix.refind.service.impl;

import com.shariarunix.refind.exception.BadRequestException;
import com.shariarunix.refind.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final String OTP_KEY_PREFIX = "auth:otp:email:";
    private static final String ATTEMPTS_KEY_PREFIX = "auth:otp:attempts:";
    private static final String COOLDOWN_KEY_PREFIX = "auth:otp:cooldown:";

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.otp.expiration-minutes:10}")
    private int expirationMinutes;

    @Value("${app.otp.max-attempts:5}")
    private int maxAttempts;

    @Value("${app.otp.cooldown-seconds:60}")
    private int cooldownSeconds;

    @Override
    public String generateOtp(String email) {
        String normalizedEmail = normalizeEmail(email);
        String cooldownKey = COOLDOWN_KEY_PREFIX + normalizedEmail;

        Long remainingCooldown = redisTemplate.getExpire(cooldownKey, TimeUnit.SECONDS);
        if (remainingCooldown != null && remainingCooldown > 0) {
            throw new BadRequestException("Please wait " + remainingCooldown + " seconds before requesting a new verification code.");
        }

        int randomCode = 100000 + secureRandom.nextInt(900000);
        String otp = String.valueOf(randomCode);

        String otpKey = OTP_KEY_PREFIX + normalizedEmail;
        String attemptsKey = ATTEMPTS_KEY_PREFIX + normalizedEmail;

        try {
            redisTemplate.opsForValue().set(otpKey, otp, Duration.ofMinutes(expirationMinutes));
            redisTemplate.opsForValue().set(cooldownKey, "active", Duration.ofSeconds(cooldownSeconds));
            redisTemplate.delete(attemptsKey);

            log.info("Generated new OTP for {} with TTL of {} minutes", normalizedEmail, expirationMinutes);
            return otp;
        } catch (Exception ex) {
            log.error("Failed to store OTP in Redis for {}: {}", normalizedEmail, ex.getMessage(), ex);
            throw new BadRequestException("Unable to generate verification code. Please try again shortly.");
        }
    }

    @Override
    public boolean verifyOtp(String email, String inputOtp) {
        String normalizedEmail = normalizeEmail(email);
        String otpKey = OTP_KEY_PREFIX + normalizedEmail;
        String attemptsKey = ATTEMPTS_KEY_PREFIX + normalizedEmail;

        String storedOtp;
        try {
            storedOtp = redisTemplate.opsForValue().get(otpKey);
        } catch (Exception ex) {
            log.error("Failed to read OTP from Redis for {}: {}", normalizedEmail, ex.getMessage(), ex);
            throw new BadRequestException("Failed to verify code due to temporary cache error. Please try again.");
        }

        if (storedOtp == null) {
            throw new BadRequestException("Verification code has expired or was not requested. Please request a new one.");
        }

        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptsKey, Duration.ofMinutes(expirationMinutes));
        }

        if (attempts != null && attempts > maxAttempts) {
            redisTemplate.delete(otpKey);
            redisTemplate.delete(attemptsKey);
            log.warn("Exceeded maximum OTP verification attempts for {}. Invalidating code.", normalizedEmail);
            throw new BadRequestException("Too many failed attempts. This code has been invalidated for security. Please request a new one.");
        }

        if (!storedOtp.equals(inputOtp.trim())) {
            int remaining = Math.max(0, maxAttempts - (attempts != null ? attempts.intValue() : 1));
            throw new BadRequestException("Invalid verification code. " + remaining + " attempts remaining.");
        }

        clearOtp(normalizedEmail);
        log.info("OTP verification successful for {}", normalizedEmail);
        return true;
    }

    @Override
    public void clearOtp(String email) {
        String normalizedEmail = normalizeEmail(email);
        try {
            redisTemplate.delete(OTP_KEY_PREFIX + normalizedEmail);
            redisTemplate.delete(ATTEMPTS_KEY_PREFIX + normalizedEmail);
            redisTemplate.delete(COOLDOWN_KEY_PREFIX + normalizedEmail);
        } catch (Exception ex) {
            log.warn("Failed to clear OTP keys in Redis for {}: {}", normalizedEmail, ex.getMessage());
        }
    }

    private String normalizeEmail(String email) {
        return email != null ? email.trim().toLowerCase() : "";
    }
}
