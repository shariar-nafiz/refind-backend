package com.shariarunix.refind.dto.user;

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
public class UserDashboardResponse {

    private UserProfileResponse profile;
    private boolean isProfileCompleted;
    private int profileCompletionPercentage;
    private long totalLostReported;
    private long totalFoundReported;
    private long totalClaimsSubmitted;
    private long totalClaimsReceived;
    private long totalItemsReunited;
    private Instant memberSince;
}
