package com.shariarunix.refind.dto.user;

import com.shariarunix.refind.entity.enums.ContactMethod;
import com.shariarunix.refind.entity.enums.ThemePreference;
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
public class UpdateUserPreferencesRequest {

    @Schema(description = "Enable email notifications", example = "true")
    private Boolean emailNotificationsEnabled;

    @Schema(description = "Enable push notifications", example = "true")
    private Boolean pushNotificationsEnabled;

    @Schema(description = "Enable smart matching alerts for lost/found items", example = "true")
    private Boolean matchAlertsEnabled;

    @Schema(description = "Enable claim status update alerts", example = "true")
    private Boolean claimAlertsEnabled;

    @Schema(description = "Preferred contact channel", example = "EMAIL")
    private ContactMethod preferredContactMethod;

    @Schema(description = "Disclose phone number once claim is approved", example = "true")
    private Boolean showPhoneOnClaimApproved;

    @Schema(description = "Disclose email once claim is approved", example = "true")
    private Boolean showEmailOnClaimApproved;

    @Schema(description = "UI language code", example = "en")
    @Size(max = 10, message = "Language code must not exceed 10 characters")
    private String language;

    @Schema(description = "UI theme preference", example = "SYSTEM")
    private ThemePreference theme;
}
