package com.shariarunix.refind.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Schema(description = "Email or Phone number", example = "shariar@refind.app")
    @NotBlank(message = "Identifier (email or phone) is required")
    private String identifier;

    @Schema(description = "Account password", example = "Password123!")
    @NotBlank(message = "Password is required")
    private String password;
}
