package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.admin.AdminUserResponse;
import com.shariarunix.refind.dto.admin.UpdateUserRoleRequest;
import com.shariarunix.refind.dto.admin.UpdateUserStatusRequest;
import com.shariarunix.refind.dto.user.AccountSetupRequest;
import com.shariarunix.refind.dto.user.AddressRequest;
import com.shariarunix.refind.dto.user.AddressResponse;
import com.shariarunix.refind.dto.user.ChangePasswordRequest;
import com.shariarunix.refind.dto.user.DeactivateAccountRequest;
import com.shariarunix.refind.dto.user.UpdateProfileRequest;
import com.shariarunix.refind.dto.user.UpdateUserPreferencesRequest;
import com.shariarunix.refind.dto.user.UserDashboardResponse;
import com.shariarunix.refind.dto.user.UserPreferencesResponse;
import com.shariarunix.refind.dto.user.UserProfileResponse;
import com.shariarunix.refind.dto.user.UserPublicProfileResponse;
import com.shariarunix.refind.entity.enums.Role;
import com.shariarunix.refind.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserProfileResponse getCurrentUserProfile(Long currentUserId);

    UserProfileResponse updateProfile(Long currentUserId, UpdateProfileRequest request);

    void changePassword(Long currentUserId, ChangePasswordRequest request);

    UserProfileResponse uploadAvatar(Long currentUserId, MultipartFile file);

    UserProfileResponse completeAccountSetup(Long currentUserId, AccountSetupRequest request);

    List<AddressResponse> getAddresses(Long currentUserId);

    AddressResponse addAddress(Long currentUserId, AddressRequest request);

    AddressResponse setDefaultAddress(Long currentUserId, Long addressId);

    void deleteAddress(Long currentUserId, Long addressId);

    UserPreferencesResponse getPreferences(Long currentUserId);

    UserPreferencesResponse updatePreferences(Long currentUserId, UpdateUserPreferencesRequest request);

    UserDashboardResponse getDashboard(Long currentUserId);

    UserPublicProfileResponse getPublicProfile(Long targetUserId);

    void deactivateAccount(Long currentUserId, DeactivateAccountRequest request, String bearerToken);

    // Administrative Moderation Operations
    Page<AdminUserResponse> getAllUsersForAdmin(String query, UserStatus status, Role role, Pageable pageable);

    AdminUserResponse getUserByIdForAdmin(Long targetUserId);

    AdminUserResponse updateUserStatusForAdmin(Long targetUserId, UpdateUserStatusRequest request);

    AdminUserResponse updateUserRoleForAdmin(Long targetUserId, UpdateUserRoleRequest request);
}
