package com.shariarunix.refind.controller;

import com.shariarunix.refind.dto.admin.AdminUserResponse;
import com.shariarunix.refind.dto.admin.UpdateUserRoleRequest;
import com.shariarunix.refind.dto.admin.UpdateUserStatusRequest;
import com.shariarunix.refind.dto.common.ApiResponse;
import com.shariarunix.refind.entity.enums.Role;
import com.shariarunix.refind.entity.enums.UserStatus;
import com.shariarunix.refind.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - User Management", description = "Endpoints for managing users, statuses, and roles by platform administrators")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Search and filter users", description = "Returns a paginated list of users with optional filtering by query, status, or role.")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "status", required = false) UserStatus status,
            @RequestParam(value = "role", required = false) Role role,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            HttpServletRequest httpRequest
    ) {
        Page<AdminUserResponse> result = userService.getAllUsersForAdmin(
                query, status, role, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(ApiResponse.paginated("Users retrieved successfully", result, httpRequest.getRequestURI()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed user profile for admin audit")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUserById(
            @PathVariable("id") Long userId,
            HttpServletRequest httpRequest
    ) {
        AdminUserResponse response = userService.getUserByIdForAdmin(userId);
        return ResponseEntity.ok(ApiResponse.success("User details retrieved successfully", response, httpRequest.getRequestURI()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update user account status (e.g. ACTIVE, BLOCKED, INACTIVE)")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUserStatus(
            @PathVariable("id") Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request,
            HttpServletRequest httpRequest
    ) {
        AdminUserResponse response = userService.updateUserStatusForAdmin(userId, request);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", response, httpRequest.getRequestURI()));
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Update user authorization role (e.g. ROLE_USER, ROLE_ADMIN)")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUserRole(
            @PathVariable("id") Long userId,
            @Valid @RequestBody UpdateUserRoleRequest request,
            HttpServletRequest httpRequest
    ) {
        AdminUserResponse response = userService.updateUserRoleForAdmin(userId, request);
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", response, httpRequest.getRequestURI()));
    }
}
