package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.user.ChangePasswordRequest;
import com.shariarunix.refind.dto.user.UpdateProfileRequest;
import com.shariarunix.refind.dto.user.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserProfileResponse getCurrentUserProfile(Long currentUserId);

    UserProfileResponse updateProfile(Long currentUserId, UpdateProfileRequest request);

    void changePassword(Long currentUserId, ChangePasswordRequest request);

    UserProfileResponse uploadAvatar(Long currentUserId, MultipartFile file);
}
