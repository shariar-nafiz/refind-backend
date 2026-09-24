package com.shariarunix.refind.service;

public interface EmailService {

    /**
     * Dispatches an email containing the 6-digit OTP code for email verification.
     *
     * @param recipientEmail Recipient's email address
     * @param recipientName  Recipient's full display name
     * @param otp            6-digit OTP code
     */
    void sendOtpEmail(String recipientEmail, String recipientName, String otp);
}
