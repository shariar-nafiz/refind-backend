package com.shariarunix.refind.dto.user;

import com.shariarunix.refind.entity.enums.Role;
import com.shariarunix.refind.entity.enums.UserStatus;
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
public class UserProfileResponse {
    private Long id;
    private String email;
    private String phone;
    private String fullName;
    private Role role;
    private UserStatus status;
    private String avatarUrl;
    private Instant createdAt;
    private Instant updatedAt;
}
