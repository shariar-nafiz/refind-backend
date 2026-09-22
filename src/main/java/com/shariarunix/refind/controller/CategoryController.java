package com.shariarunix.refind.controller;

import com.shariarunix.refind.dto.category.CategoryResponse;
import com.shariarunix.refind.dto.common.ApiResponse;
import com.shariarunix.refind.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Public category endpoints for item classification")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all active categories", description = "Retrieves all currently active categories ordered by display order")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllActiveCategories(HttpServletRequest request) {
        List<CategoryResponse> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(ApiResponse.success("Active categories retrieved successfully", categories, request.getRequestURI()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieves a single category by its primary key identifier")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id, HttpServletRequest request) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category, request.getRequestURI()));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get category by slug", description = "Retrieves a single category by its unique URL-friendly slug")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(@PathVariable String slug, HttpServletRequest request) {
        CategoryResponse category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category, request.getRequestURI()));
    }
}
