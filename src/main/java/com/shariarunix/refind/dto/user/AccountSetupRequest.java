package com.shariarunix.refind.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
public class AccountSetupRequest {

    @Schema(description = "Full display name", example = "Md Shariar")
    @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
    private String fullName;

    @Schema(description = "Short personal biography or introduction", example = "Student at Southeast University, commuting daily.")
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    @Schema(description = "Alternative contact number or WhatsApp", example = "+8801800000000")
    @Size(max = 50, message = "Secondary phone must not exceed 50 characters")
    private String secondaryPhone;

    @Schema(description = "Avatar URL if uploaded beforehand", example = "/uploads/avatars/avatar.jpg")
    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    private String avatarUrl;

    @Valid
    @Schema(description = "Primary default address for local area matching and returns")
    private AddressRequest defaultAddress;

    @Valid
    @Schema(description = "Initial notification, privacy, and theme preferences")
    private UpdateUserPreferencesRequest preferences;
}
