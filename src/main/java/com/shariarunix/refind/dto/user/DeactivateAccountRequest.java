package com.shariarunix.refind.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class DeactivateAccountRequest {

    @Schema(description = "Current account password for verification", example = "Secret12345")
    @NotBlank(message = "Password is required to confirm account deactivation")
    private String password;

    @Schema(description = "Optional reason for deactivating account", example = "No longer need the service")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
}
