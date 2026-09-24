package com.shariarunix.refind.service.impl;

import com.shariarunix.refind.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ResendEmailServiceImpl implements EmailService {

    @Value("${resend.api.key:}")
    private String apiKey;

    @Value("${resend.api.url:https://api.resend.com}")
    private String apiUrl;

    @Value("${resend.from.email:no-reply@refind.shariarunix.me}")
    private String fromEmail;

    @Value("${resend.from.name:ReFind}")
    private String fromName;

    @Value("${resend.dev-mode:false}")
    private boolean devMode;

    @Value("${app.otp.expiration-minutes:10}")
    private int expirationMinutes;

    private final RestClient restClient;

    public ResendEmailServiceImpl() {
        this.restClient = RestClient.builder().build();
    }

    @Override
    public void sendOtpEmail(String recipientEmail, String recipientName, String otp) {
        String displayName = StringUtils.hasText(recipientName) ? recipientName : "Valued Member";
        String senderFormatted = String.format("%s <%s>", fromName, fromEmail);
        String subject = "Verify your email address - ReFind";

        if (devMode) {
            log.info("==================== [DEV EMAIL OTP DISPATCH] ====================");
            log.info("To: {} ({})", recipientEmail, displayName);
            log.info("From: {}", senderFormatted);
            log.info("Subject: {}", subject);
            log.info("OTP Code: {}", otp);
            log.info("Expires in: {} minutes", expirationMinutes);
            log.info("Notice: Running in Dev mode (RESEND_DEV_MODE=true). External email skipped.");
            log.info("==================================================================");
            return;
        }

        try {
            String htmlContent = buildOtpHtmlEmail(displayName, otp, expirationMinutes);

            Map<String, Object> payload = Map.of(
                    "from", senderFormatted,
                    "to", List.of(recipientEmail),
                    "subject", subject,
                    "html", htmlContent
            );

            restClient.post()
                    .uri(apiUrl + "/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully dispatched OTP verification email to {} via Resend", recipientEmail);
        } catch (Exception ex) {
            log.error("Failed to send OTP verification email to {} via Resend: {}", recipientEmail, ex.getMessage(), ex);
            // In dev or local testing environments, ensure the OTP is still logged so testing isn't blocked
            log.warn("[FALLBACK LOG] Verification OTP for {}: {}", recipientEmail, otp);
        }
    }

    private String buildOtpHtmlEmail(String name, String otp, int validMinutes) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Verify your ReFind Account</title>
              <style>
                body { margin: 0; padding: 0; background-color: #f8fafc; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; color: #0f172a; }
                .wrapper { max-width: 580px; margin: 40px auto; background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05); }
                .header { background: #0f172a; padding: 28px 32px; text-align: center; }
                .header h1 { margin: 0; color: #ffffff; font-size: 24px; font-weight: 700; letter-spacing: -0.5px; }
                .header p { margin: 6px 0 0 0; color: #94a3b8; font-size: 13px; }
                .content { padding: 36px 32px; }
                .greeting { font-size: 16px; line-height: 24px; margin-bottom: 16px; color: #334155; }
                .otp-box { background: #f1f5f9; border: 1px dashed #cbd5e1; border-radius: 10px; padding: 20px; text-align: center; margin: 28px 0; }
                .otp-label { font-size: 12px; font-weight: 600; text-transform: uppercase; color: #64748b; letter-spacing: 1px; margin-bottom: 8px; }
                .otp-code { font-size: 34px; font-weight: 800; letter-spacing: 8px; color: #0f172a; font-family: 'Courier New', Courier, monospace; }
                .expiry-note { font-size: 13px; color: #64748b; margin-top: 10px; }
                .security-warning { background: #fffbeb; border-left: 4px solid #f59e0b; padding: 12px 16px; border-radius: 4px; font-size: 13px; color: #92400e; margin-top: 24px; line-height: 20px; }
                .footer { background: #f8fafc; border-top: 1px solid #e2e8f0; padding: 20px 32px; text-align: center; font-size: 12px; color: #94a3b8; line-height: 18px; }
              </style>
            </head>
            <body>
              <div class="wrapper">
                <div class="header">
                  <h1>ReFind</h1>
                  <p>Lost & Found Platform for Bangladesh</p>
                </div>
                <div class="content">
                  <div class="greeting">
                    Hello <strong>%s</strong>,
                  </div>
                  <p style="font-size: 14px; line-height: 22px; color: #475569; margin: 0 0 20px 0;">
                    Thank you for signing up with ReFind. To verify your email address and activate your account, please enter the following 6-digit one-time password (OTP):
                  </p>
                  <div class="otp-box">
                    <div class="otp-label">Verification Code</div>
                    <div class="otp-code">%s</div>
                    <div class="expiry-note">This code will expire in <strong>%d minutes</strong>.</div>
                  </div>
                  <div class="security-warning">
                    <strong>Security Notice:</strong> Never share this code with anyone. ReFind representatives will never ask for your verification code.
                  </div>
                </div>
                <div class="footer">
                  This is an automated message sent by ReFind. If you did not create an account, you can safely ignore this email.<br>
                  &copy; 2026 ReFind. All rights reserved.
                </div>
              </div>
            </body>
            </html>
            """.formatted(name, otp, validMinutes);
    }
}
