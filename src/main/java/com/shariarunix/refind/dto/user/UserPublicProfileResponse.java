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
public class UserPublicProfileResponse {

    private Long id;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private String city;
    private String country;
    private long totalItemsReunited;
    private Instant memberSince;
}
