package com.shariarunix.refind.service.impl;

import com.shariarunix.refind.dto.admin.AdminUserResponse;
import com.shariarunix.refind.dto.admin.UpdateUserRoleRequest;
import com.shariarunix.refind.dto.admin.UpdateUserStatusRequest;
import com.shariarunix.refind.dto.media.MediaResponse;
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
import com.shariarunix.refind.entity.User;
import com.shariarunix.refind.entity.UserAddress;
import com.shariarunix.refind.entity.UserPreference;
import com.shariarunix.refind.entity.enums.AddressType;
import com.shariarunix.refind.entity.enums.ContactMethod;
import com.shariarunix.refind.entity.enums.Role;
import com.shariarunix.refind.entity.enums.ThemePreference;
import com.shariarunix.refind.entity.enums.UserStatus;
import com.shariarunix.refind.exception.BadRequestException;
import com.shariarunix.refind.exception.ResourceNotFoundException;
import com.shariarunix.refind.repository.UserAddressRepository;
import com.shariarunix.refind.repository.UserPreferenceRepository;
import com.shariarunix.refind.repository.UserRepository;
import com.shariarunix.refind.security.JwtTokenProvider;
import com.shariarunix.refind.service.MediaService;
import com.shariarunix.refind.service.RedisTokenService;
import com.shariarunix.refind.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTokenService redisTokenService;
    private final MediaService mediaService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(Long currentUserId) {
        User user = getUserOrThrow(currentUserId);
        return mapToUserProfile(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long currentUserId, UpdateProfileRequest request) {
        User user = getUserOrThrow(currentUserId);

        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName().trim());
        }

        if (StringUtils.hasText(request.getPhone())) {
            String newPhone = request.getPhone().trim();
            if (!newPhone.equals(user.getPhone()) && userRepository.existsByPhone(newPhone)) {
                throw new BadRequestException("Phone number already registered to another account: " + newPhone);
            }
            user.setPhone(newPhone);
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl().trim());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio().trim());
        }

        if (request.getSecondaryPhone() != null) {
            user.setSecondaryPhone(request.getSecondaryPhone().trim());
        }

        user = userRepository.save(user);
        log.info("Updated profile for user ID: {}", user.getId());
        return mapToUserProfile(user);
    }

    @Override
    @Transactional
    public void changePassword(Long currentUserId, ChangePasswordRequest request) {
        User user = getUserOrThrow(currentUserId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Incorrect current password");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BadRequestException("New password cannot be the same as the current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        redisTokenService.revokeRefreshToken(user.getId());
        log.info("Changed password and revoked active refresh tokens for user ID: {}", user.getId());
    }

    @Override
    @Transactional
    public UserProfileResponse uploadAvatar(Long currentUserId, MultipartFile file) {
        User user = getUserOrThrow(currentUserId);
        MediaResponse mediaResponse = mediaService.uploadMedia(file, user.getId(), "avatars");
        user.setAvatarUrl(mediaResponse.getFileUrl());
        user = userRepository.save(user);
        log.info("Updated avatar for user ID: {} to {}", user.getId(), user.getAvatarUrl());
        return mapToUserProfile(user);
    }

    @Override
    @Transactional
    public UserProfileResponse completeAccountSetup(Long currentUserId, AccountSetupRequest request) {
        User user = getUserOrThrow(currentUserId);

        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio().trim());
        }
        if (request.getSecondaryPhone() != null) {
            user.setSecondaryPhone(request.getSecondaryPhone().trim());
        }
        if (StringUtils.hasText(request.getAvatarUrl())) {
            user.setAvatarUrl(request.getAvatarUrl().trim());
        }

        // Handle Default Address if provided
        if (request.getDefaultAddress() != null) {
            AddressRequest addrReq = request.getDefaultAddress();
            if (StringUtils.hasText(addrReq.getCountry()) && StringUtils.hasText(addrReq.getCity()) && StringUtils.hasText(addrReq.getStreetAddress())) {
                List<UserAddress> existingAddresses = userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(currentUserId);
                for (UserAddress existing : existingAddresses) {
                    existing.setDefault(false);
                    userAddressRepository.save(existing);
                }

                UserAddress address = UserAddress.builder()
                        .user(user)
                        .addressType(addrReq.getAddressType() != null ? addrReq.getAddressType() : AddressType.HOME)
                        .country(addrReq.getCountry().trim())
                        .state(addrReq.getState() != null ? addrReq.getState().trim() : null)
                        .city(addrReq.getCity().trim())
                        .streetAddress(addrReq.getStreetAddress().trim())
                        .addressLine2(addrReq.getAddressLine2() != null ? addrReq.getAddressLine2().trim() : null)
                        .postalCode(addrReq.getPostalCode() != null ? addrReq.getPostalCode().trim() : null)
                        .isDefault(true)
                        .build();
                userAddressRepository.save(address);
            }
        }

        // Handle Preferences if provided
        if (request.getPreferences() != null) {
            updateUserPreferencesInternal(user, request.getPreferences());
        } else {
            getOrCreateDefaultPreferences(user);
        }

        user.setProfileCompleted(true);
        user = userRepository.save(user);
        log.info("Completed account setup for user ID: {}", user.getId());

        return mapToUserProfile(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(Long currentUserId) {
        getUserOrThrow(currentUserId);
        return userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(currentUserId).stream()
                .map(this::mapToAddressResponse)
                .toList();
    }

    @Override
    @Transactional
    public AddressResponse addAddress(Long currentUserId, AddressRequest request) {
        User user = getUserOrThrow(currentUserId);
        List<UserAddress> existing = userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(currentUserId);

        boolean makeDefault = Boolean.TRUE.equals(request.getIsDefault()) || existing.isEmpty();

        if (makeDefault && !existing.isEmpty()) {
            for (UserAddress addr : existing) {
                addr.setDefault(false);
                userAddressRepository.save(addr);
            }
        }

        UserAddress address = UserAddress.builder()
                .user(user)
                .addressType(request.getAddressType() != null ? request.getAddressType() : AddressType.HOME)
                .country(request.getCountry().trim())
                .state(request.getState() != null ? request.getState().trim() : null)
                .city(request.getCity().trim())
                .streetAddress(request.getStreetAddress().trim())
                .addressLine2(request.getAddressLine2() != null ? request.getAddressLine2().trim() : null)
                .postalCode(request.getPostalCode() != null ? request.getPostalCode().trim() : null)
                .isDefault(makeDefault)
                .build();

        address = userAddressRepository.save(address);
        log.info("Added address ID: {} for user ID: {}", address.getId(), currentUserId);
        return mapToAddressResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(Long currentUserId, Long addressId) {
        getUserOrThrow(currentUserId);
        UserAddress targetAddress = userAddressRepository.findByIdAndUserId(addressId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        List<UserAddress> allAddresses = userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(currentUserId);
        for (UserAddress addr : allAddresses) {
            addr.setDefault(addr.getId().equals(addressId));
            userAddressRepository.save(addr);
        }

        log.info("Set address ID: {} as default for user ID: {}", addressId, currentUserId);
        return mapToAddressResponse(targetAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long currentUserId, Long addressId) {
        getUserOrThrow(currentUserId);
        UserAddress address = userAddressRepository.findByIdAndUserId(addressId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        boolean wasDefault = address.isDefault();
        userAddressRepository.delete(address);

        if (wasDefault) {
            List<UserAddress> remaining = userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(currentUserId);
            if (!remaining.isEmpty()) {
                UserAddress newDefault = remaining.get(0);
                newDefault.setDefault(true);
                userAddressRepository.save(newDefault);
            }
        }
        log.info("Deleted address ID: {} for user ID: {}", addressId, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserPreferencesResponse getPreferences(Long currentUserId) {
        User user = getUserOrThrow(currentUserId);
        UserPreference pref = userPreferenceRepository.findByUserId(currentUserId)
                .orElseGet(() -> getOrCreateDefaultPreferences(user));
        return mapToPreferencesResponse(pref);
    }

    @Override
    @Transactional
    public UserPreferencesResponse updatePreferences(Long currentUserId, UpdateUserPreferencesRequest request) {
        User user = getUserOrThrow(currentUserId);
        UserPreference pref = updateUserPreferencesInternal(user, request);
        return mapToPreferencesResponse(pref);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDashboardResponse getDashboard(Long currentUserId) {
        User user = getUserOrThrow(currentUserId);
        UserProfileResponse profileResponse = mapToUserProfile(user);

        // Calculate profile completion percentage (0 - 100)
        int completionScore = 0;
        if (StringUtils.hasText(user.getFullName())) completionScore += 20;
        if (StringUtils.hasText(user.getEmail()) || StringUtils.hasText(user.getPhone())) completionScore += 20;
        if (StringUtils.hasText(user.getAvatarUrl())) completionScore += 20;
        if (StringUtils.hasText(user.getBio())) completionScore += 20;
        if (profileResponse.getDefaultAddress() != null) completionScore += 20;

        return UserDashboardResponse.builder()
                .profile(profileResponse)
                .isProfileCompleted(user.isProfileCompleted())
                .profileCompletionPercentage(completionScore)
                .totalLostReported(0L)
                .totalFoundReported(0L)
                .totalClaimsSubmitted(0L)
                .totalClaimsReceived(0L)
                .totalItemsReunited(0L)
                .memberSince(user.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserPublicProfileResponse getPublicProfile(Long targetUserId) {
        User user = getUserOrThrow(targetUserId);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResourceNotFoundException("User profile not found or is currently inactive");
        }

        UserAddress defaultAddress = userAddressRepository.findByUserIdAndIsDefaultTrue(targetUserId).orElse(null);

        return UserPublicProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .city(defaultAddress != null ? defaultAddress.getCity() : null)
                .country(defaultAddress != null ? defaultAddress.getCountry() : null)
                .totalItemsReunited(0L)
                .memberSince(user.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deactivateAccount(Long currentUserId, DeactivateAccountRequest request, String bearerToken) {
        User user = getUserOrThrow(currentUserId);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Incorrect password for account deactivation");
        }

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        // Revoke active sessions in Redis
        redisTokenService.revokeRefreshToken(currentUserId);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String jwt = bearerToken.substring(7);
            if (jwtTokenProvider.validateToken(jwt)) {
                long remainingMs = jwtTokenProvider.getRemainingExpirationMs(jwt);
                if (remainingMs > 0) {
                    redisTokenService.blacklistToken(jwt, remainingMs);
                }
            }
        }
        log.info("Deactivated user ID: {}. Reason: {}", currentUserId, request.getReason());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getAllUsersForAdmin(String query, UserStatus status, Role role, Pageable pageable) {
        Page<User> users = userRepository.searchUsers(query, status, role, pageable);
        return users.map(this::mapToAdminUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponse getUserByIdForAdmin(Long targetUserId) {
        User user = getUserOrThrow(targetUserId);
        return mapToAdminUserResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse updateUserStatusForAdmin(Long targetUserId, UpdateUserStatusRequest request) {
        User user = getUserOrThrow(targetUserId);
        user.setStatus(request.getStatus());
        user = userRepository.save(user);

        if (request.getStatus() == UserStatus.BLOCKED || request.getStatus() == UserStatus.INACTIVE) {
            redisTokenService.revokeRefreshToken(targetUserId);
        }

        log.info("Admin updated status of user ID: {} to {}", targetUserId, request.getStatus());
        return mapToAdminUserResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse updateUserRoleForAdmin(Long targetUserId, UpdateUserRoleRequest request) {
        User user = getUserOrThrow(targetUserId);
        user.setRole(request.getRole());
        user = userRepository.save(user);

        log.info("Admin updated role of user ID: {} to {}", targetUserId, request.getRole());
        return mapToAdminUserResponse(user);
    }

    // Helper Methods
    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserPreference getOrCreateDefaultPreferences(User user) {
        return userPreferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserPreference pref = UserPreference.builder()
                            .user(user)
                            .emailNotificationsEnabled(true)
                            .pushNotificationsEnabled(true)
                            .matchAlertsEnabled(true)
                            .claimAlertsEnabled(true)
                            .preferredContactMethod(ContactMethod.EMAIL)
                            .showPhoneOnClaimApproved(true)
                            .showEmailOnClaimApproved(true)
                            .language("en")
                            .theme(ThemePreference.SYSTEM)
                            .build();
                    return userPreferenceRepository.save(pref);
                });
    }

    private UserPreference updateUserPreferencesInternal(User user, UpdateUserPreferencesRequest request) {
        UserPreference pref = userPreferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> UserPreference.builder().user(user).build());

        if (request.getEmailNotificationsEnabled() != null) {
            pref.setEmailNotificationsEnabled(request.getEmailNotificationsEnabled());
        }
        if (request.getPushNotificationsEnabled() != null) {
            pref.setPushNotificationsEnabled(request.getPushNotificationsEnabled());
        }
        if (request.getMatchAlertsEnabled() != null) {
            pref.setMatchAlertsEnabled(request.getMatchAlertsEnabled());
        }
        if (request.getClaimAlertsEnabled() != null) {
            pref.setClaimAlertsEnabled(request.getClaimAlertsEnabled());
        }
        if (request.getPreferredContactMethod() != null) {
            pref.setPreferredContactMethod(request.getPreferredContactMethod());
        }
        if (request.getShowPhoneOnClaimApproved() != null) {
            pref.setShowPhoneOnClaimApproved(request.getShowPhoneOnClaimApproved());
        }
        if (request.getShowEmailOnClaimApproved() != null) {
            pref.setShowEmailOnClaimApproved(request.getShowEmailOnClaimApproved());
        }
        if (StringUtils.hasText(request.getLanguage())) {
            pref.setLanguage(request.getLanguage().trim());
        }
        if (request.getTheme() != null) {
            pref.setTheme(request.getTheme());
        }

        return userPreferenceRepository.save(pref);
    }

    private UserProfileResponse mapToUserProfile(User user) {
        AddressResponse defaultAddress = userAddressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                .map(this::mapToAddressResponse)
                .orElse(null);

        UserPreferencesResponse preferences = userPreferenceRepository.findByUserId(user.getId())
                .map(this::mapToPreferencesResponse)
                .orElse(null);

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .secondaryPhone(user.getSecondaryPhone())
                .isProfileCompleted(user.isProfileCompleted())
                .defaultAddress(defaultAddress)
                .preferences(preferences)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private AddressResponse mapToAddressResponse(UserAddress address) {
        return AddressResponse.builder()
                .id(address.getId())
                .addressType(address.getAddressType())
                .country(address.getCountry())
                .state(address.getState())
                .city(address.getCity())
                .streetAddress(address.getStreetAddress())
                .addressLine2(address.getAddressLine2())
                .postalCode(address.getPostalCode())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }

    private UserPreferencesResponse mapToPreferencesResponse(UserPreference pref) {
        return UserPreferencesResponse.builder()
                .emailNotificationsEnabled(pref.isEmailNotificationsEnabled())
                .pushNotificationsEnabled(pref.isPushNotificationsEnabled())
                .matchAlertsEnabled(pref.isMatchAlertsEnabled())
                .claimAlertsEnabled(pref.isClaimAlertsEnabled())
                .preferredContactMethod(pref.getPreferredContactMethod())
                .showPhoneOnClaimApproved(pref.isShowPhoneOnClaimApproved())
                .showEmailOnClaimApproved(pref.isShowEmailOnClaimApproved())
                .language(pref.getLanguage())
                .theme(pref.getTheme())
                .updatedAt(pref.getUpdatedAt())
                .build();
    }

    private AdminUserResponse mapToAdminUserResponse(User user) {
        List<AddressResponse> addresses = userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(user.getId()).stream()
                .map(this::mapToAddressResponse)
                .toList();

        UserPreferencesResponse preferences = userPreferenceRepository.findByUserId(user.getId())
                .map(this::mapToPreferencesResponse)
                .orElse(null);

        return AdminUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .secondaryPhone(user.getSecondaryPhone())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .role(user.getRole())
                .status(user.getStatus())
                .isProfileCompleted(user.isProfileCompleted())
                .addresses(addresses)
                .preferences(preferences)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
