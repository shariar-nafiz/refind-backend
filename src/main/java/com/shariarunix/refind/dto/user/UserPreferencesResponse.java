package com.shariarunix.refind.dto.user;

import com.shariarunix.refind.entity.enums.ContactMethod;
import com.shariarunix.refind.entity.enums.ThemePreference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesResponse {

    private boolean emailNotificationsEnabled;
    private boolean pushNotificationsEnabled;
    private boolean matchAlertsEnabled;
    private boolean claimAlertsEnabled;
    private ContactMethod preferredContactMethod;
    private boolean showPhoneOnClaimApproved;
    private boolean showEmailOnClaimApproved;
    private String language;
    private ThemePreference theme;
    private Instant updatedAt;
}
