package com.shariarunix.refind.controller;

import com.shariarunix.refind.dto.common.ApiResponse;
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
import com.shariarunix.refind.security.UserPrincipal;
import com.shariarunix.refind.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing user profiles, addresses, preferences, and account security")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletRequest httpRequest
    ) {
        UserProfileResponse response = userService.getCurrentUserProfile(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", response, httpRequest.getRequestURI()));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile information")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest
    ) {
        UserProfileResponse response = userService.updateProfile(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("User profile updated successfully", response, httpRequest.getRequestURI()));
    }

    @PostMapping("/me/setup")
    @Operation(summary = "Complete initial account onboarding setup", description = "Sets up display name, bio, default address, and notification preferences, marking profile as completed.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> completeAccountSetup(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AccountSetupRequest request,
            HttpServletRequest httpRequest
    ) {
        UserProfileResponse response = userService.completeAccountSetup(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Account setup completed successfully", response, httpRequest.getRequestURI()));
    }

    @GetMapping("/me/dashboard")
    @Operation(summary = "Get user dashboard statistics and profile completeness")
    public ResponseEntity<ApiResponse<UserDashboardResponse>> getDashboard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletRequest httpRequest
    ) {
        UserDashboardResponse response = userService.getDashboard(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("User dashboard retrieved successfully", response, httpRequest.getRequestURI()));
    }

    @PatchMapping("/me/password")
    @Operation(summary = "Change current user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        userService.changePassword(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully", httpRequest.getRequestURI()));
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and update user profile avatar", description = "Uploads an avatar image (JPEG, PNG, WebP) and updates the current user's profile avatar URL.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> uploadAvatar(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest
    ) {
        UserProfileResponse response = userService.uploadAvatar(userPrincipal.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Avatar updated successfully", response, httpRequest.getRequestURI()));
    }

    // --- Address Management Endpoints ---

    @GetMapping("/me/addresses")
    @Operation(summary = "Get all saved addresses for current user")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletRequest httpRequest
    ) {
        List<AddressResponse> response = userService.getAddresses(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("User addresses retrieved successfully", response, httpRequest.getRequestURI()));
    }

    @PostMapping("/me/addresses")
    @Operation(summary = "Add a new address to user profile")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddressRequest request,
            HttpServletRequest httpRequest
    ) {
        AddressResponse response = userService.addAddress(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address added successfully", response, httpRequest.getRequestURI()));
    }

    @PatchMapping("/me/addresses/{id}/default")
    @Operation(summary = "Set an address as the default primary location")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") Long addressId,
            HttpServletRequest httpRequest
    ) {
        AddressResponse response = userService.setDefaultAddress(userPrincipal.getId(), addressId);
        return ResponseEntity.ok(ApiResponse.success("Default address updated successfully", response, httpRequest.getRequestURI()));
    }

    @DeleteMapping("/me/addresses/{id}")
    @Operation(summary = "Delete an address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") Long addressId,
            HttpServletRequest httpRequest
    ) {
        userService.deleteAddress(userPrincipal.getId(), addressId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", httpRequest.getRequestURI()));
    }

    // --- Preferences Endpoints ---

    @GetMapping("/me/preferences")
    @Operation(summary = "Get notification and privacy preferences")
    public ResponseEntity<ApiResponse<UserPreferencesResponse>> getPreferences(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletRequest httpRequest
    ) {
        UserPreferencesResponse response = userService.getPreferences(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("User preferences retrieved successfully", response, httpRequest.getRequestURI()));
    }

    @PutMapping("/me/preferences")
    @Operation(summary = "Update notification, contact, and privacy preferences")
    public ResponseEntity<ApiResponse<UserPreferencesResponse>> updatePreferences(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateUserPreferencesRequest request,
            HttpServletRequest httpRequest
    ) {
        UserPreferencesResponse response = userService.updatePreferences(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("User preferences updated successfully", response, httpRequest.getRequestURI()));
    }

    // --- Account Deactivation ---

    @PostMapping("/me/deactivate")
    @Operation(summary = "Deactivate user account", description = "Requires password confirmation. Soft-deactivates account and terminates all active sessions in Redis.")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody DeactivateAccountRequest request,
            HttpServletRequest httpRequest
    ) {
        String bearerToken = httpRequest.getHeader("Authorization");
        userService.deactivateAccount(userPrincipal.getId(), request, bearerToken);
        return ResponseEntity.ok(ApiResponse.success("Account deactivated successfully", httpRequest.getRequestURI()));
    }

    // --- Public Profile View ---

    @GetMapping("/{id}/public")
    @Operation(summary = "View public user profile", description = "Returns safe public information omitting private contact credentials.")
    @SecurityRequirement(name = "") // No auth strictly required
    public ResponseEntity<ApiResponse<UserPublicProfileResponse>> getPublicProfile(
            @PathVariable("id") Long targetUserId,
            HttpServletRequest httpRequest
    ) {
        UserPublicProfileResponse response = userService.getPublicProfile(targetUserId);
        return ResponseEntity.ok(ApiResponse.success("Public profile retrieved successfully", response, httpRequest.getRequestURI()));
    }
}
