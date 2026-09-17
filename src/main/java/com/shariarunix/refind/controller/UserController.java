package com.shariarunix.refind.controller;

import com.shariarunix.refind.dto.common.ApiResponse;
import com.shariarunix.refind.dto.user.ChangePasswordRequest;
import com.shariarunix.refind.dto.user.UpdateProfileRequest;
import com.shariarunix.refind.dto.user.UserProfileResponse;
import com.shariarunix.refind.security.UserPrincipal;
import com.shariarunix.refind.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing user profiles and account security")
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
    @Operation(summary = "Update current user profile information (full name, phone, avatar)")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest
    ) {
        UserProfileResponse response = userService.updateProfile(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("User profile updated successfully", response, httpRequest.getRequestURI()));
    }

    @PatchMapping("/me/password")
    @Operation(summary = "Change current user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        userService.changePassword(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Password updated successfully", httpRequest.getRequestURI()));
    }
}
