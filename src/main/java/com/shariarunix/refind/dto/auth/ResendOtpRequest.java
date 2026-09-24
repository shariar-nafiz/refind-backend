package com.shariarunix.refind.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResendOtpRequest {

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    @Schema(description = "Registered email address awaiting verification", example = "user@example.com")
    private String email;
}
