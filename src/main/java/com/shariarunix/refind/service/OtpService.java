package com.shariarunix.refind.service;

public interface OtpService {

    /**
     * Generates a secure 6-digit OTP for the given email, enforces resend cooldown,
     * and stores it in Redis with configured TTL.
     *
     * @param email Recipient email address
     * @return Generated 6-digit OTP
     */
    String generateOtp(String email);

    /**
     * Verifies the provided OTP against the stored code in Redis.
     * Enforces maximum attempt limits and cleans up on success.
     *
     * @param email    User email
     * @param inputOtp 6-digit code entered by user
     * @return true if valid
     */
    boolean verifyOtp(String email, String inputOtp);

    /**
     * Clears all OTP-related keys for the given email from Redis.
     *
     * @param email User email
     */
    void clearOtp(String email);
}
