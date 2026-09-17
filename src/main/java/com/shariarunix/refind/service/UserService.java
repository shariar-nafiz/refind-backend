package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.user.ChangePasswordRequest;
import com.shariarunix.refind.dto.user.UpdateProfileRequest;
import com.shariarunix.refind.dto.user.UserProfileResponse;

public interface UserService {

    UserProfileResponse getCurrentUserProfile(Long currentUserId);

    UserProfileResponse updateProfile(Long currentUserId, UpdateProfileRequest request);

    void changePassword(Long currentUserId, ChangePasswordRequest request);
}
