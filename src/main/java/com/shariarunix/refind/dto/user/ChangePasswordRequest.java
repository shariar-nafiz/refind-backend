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
public class ChangePasswordRequest {

    @Schema(description = "Current account password", example = "OldPassword123!")
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @Schema(description = "New account password (min 6 chars)", example = "NewPassword123!")
    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 100, message = "New password must be at least 6 characters long")
    private String newPassword;
}
