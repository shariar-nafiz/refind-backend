package com.shariarunix.refind.controller;

import com.shariarunix.refind.dto.category.CategoryResponse;
import com.shariarunix.refind.dto.category.CreateCategoryRequest;
import com.shariarunix.refind.dto.category.UpdateCategoryRequest;
import com.shariarunix.refind.dto.common.ApiResponse;
import com.shariarunix.refind.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Admin - Category Management", description = "Endpoints for managing categories by platform administrators")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories (including inactive)", description = "Retrieves all categories in display order for admin overview")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(HttpServletRequest request) {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("All categories retrieved successfully", categories, request.getRequestURI()));
    }

    @PostMapping
    @Operation(summary = "Create new category", description = "Registers a new category in the catalog")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest createRequest,
            HttpServletRequest request
    ) {
        CategoryResponse category = categoryService.createCategory(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", category, request.getRequestURI()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Updates details of an existing category")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest updateRequest,
            HttpServletRequest request
    ) {
        CategoryResponse category = categoryService.updateCategory(id, updateRequest);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category, request.getRequestURI()));
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle category active status", description = "Enables or disables an existing category")
    public ResponseEntity<ApiResponse<CategoryResponse>> toggleCategoryStatus(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        CategoryResponse category = categoryService.toggleCategoryStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Category status updated successfully", category, request.getRequestURI()));
    }
}
