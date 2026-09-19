package com.shariarunix.refind.dto.admin;

import com.shariarunix.refind.dto.user.AddressResponse;
import com.shariarunix.refind.dto.user.UserPreferencesResponse;
import com.shariarunix.refind.entity.enums.Role;
import com.shariarunix.refind.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {

    private Long id;
    private String email;
    private String phone;
    private String secondaryPhone;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private Role role;
    private UserStatus status;
    private boolean isProfileCompleted;
    private List<AddressResponse> addresses;
    private UserPreferencesResponse preferences;
    private Instant createdAt;
    private Instant updatedAt;
}
