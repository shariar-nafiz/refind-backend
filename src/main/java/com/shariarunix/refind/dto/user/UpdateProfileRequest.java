package com.shariarunix.refind.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class UpdateProfileRequest {

    @Schema(description = "Updated full name", example = "Md Shariar")
    @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
    private String fullName;

    @Schema(description = "Updated phone number", example = "+8801700000000")
    private String phone;

    @Schema(description = "Updated avatar URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;
}
