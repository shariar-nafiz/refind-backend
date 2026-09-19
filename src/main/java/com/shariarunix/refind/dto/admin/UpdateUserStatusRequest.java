package com.shariarunix.refind.dto.admin;

import com.shariarunix.refind.entity.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
public class UpdateUserStatusRequest {

    @Schema(description = "New user status", example = "ACTIVE")
    @NotNull(message = "User status cannot be null")
    private UserStatus status;

    @Schema(description = "Optional moderation reason for status change", example = "Account suspended due to policy violation")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
}
