package com.shariarunix.refind.service.impl;

import com.shariarunix.refind.service.RedisTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTokenServiceImpl implements RedisTokenService {

    private static final String BLACKLIST_PREFIX = "refind:blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "refind:refresh:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void blacklistToken(String token, long remainingTtlMs) {
        if (remainingTtlMs <= 0) {
            return;
        }
        try {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key, "revoked", Duration.ofMillis(remainingTtlMs));
            log.debug("Blacklisted token with remaining TTL: {}ms", remainingTtlMs);
        } catch (Exception ex) {
            log.error("Failed to blacklist token in Redis: {}", ex.getMessage());
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception ex) {
            log.error("Failed to check token blacklist in Redis: {}", ex.getMessage());
            return false;
        }
    }

    @Override
    public void storeRefreshToken(Long userId, String refreshToken, long ttlMs) {
        if (userId == null || refreshToken == null || ttlMs <= 0) {
            return;
        }
        try {
            String key = REFRESH_TOKEN_PREFIX + userId;
            redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(ttlMs));
            log.debug("Stored active refresh token for user ID: {}", userId);
        } catch (Exception ex) {
            log.error("Failed to store refresh token in Redis for user {}: {}", userId, ex.getMessage());
        }
    }

    @Override
    public boolean validateRefreshToken(Long userId, String refreshToken) {
        if (userId == null || refreshToken == null) {
            return false;
        }
        try {
            String key = REFRESH_TOKEN_PREFIX + userId;
            String storedToken = redisTemplate.opsForValue().get(key);
            return storedToken != null && storedToken.equals(refreshToken);
        } catch (Exception ex) {
            log.error("Failed to validate refresh token in Redis for user {}: {}", userId, ex.getMessage());
            return false;
        }
    }

    @Override
    public void revokeRefreshToken(Long userId) {
        if (userId == null) {
            return;
        }
        try {
            String key = REFRESH_TOKEN_PREFIX + userId;
            redisTemplate.delete(key);
            log.debug("Revoked refresh token for user ID: {}", userId);
        } catch (Exception ex) {
            log.error("Failed to revoke refresh token in Redis for user {}: {}", userId, ex.getMessage());
        }
    }
}
