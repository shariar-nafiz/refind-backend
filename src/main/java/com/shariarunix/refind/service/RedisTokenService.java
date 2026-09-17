package com.shariarunix.refind.service;

public interface RedisTokenService {

    /**
     * Blacklists an access token in Redis until it naturally expires.
     *
     * @param token the JWT access token string
     * @param remainingTtlMs remaining validity duration in milliseconds
     */
    void blacklistToken(String token, long remainingTtlMs);

    /**
     * Checks if an access token has been revoked / blacklisted.
     *
     * @param token the JWT access token string
     * @return true if blacklisted, false otherwise
     */
    boolean isTokenBlacklisted(String token);

    /**
     * Stores the active refresh token for a user.
     *
     * @param userId the user primary key ID
     * @param refreshToken the refresh token string
     * @param ttlMs validity duration in milliseconds
     */
    void storeRefreshToken(Long userId, String refreshToken, long ttlMs);

    /**
     * Validates whether the given refresh token matches the active token in Redis for the user.
     *
     * @param userId the user primary key ID
     * @param refreshToken the refresh token string to check
     * @return true if matches, false otherwise
     */
    boolean validateRefreshToken(Long userId, String refreshToken);

    /**
     * Revokes the user's active refresh token from Redis.
     *
     * @param userId the user primary key ID
     */
    void revokeRefreshToken(Long userId);
}
