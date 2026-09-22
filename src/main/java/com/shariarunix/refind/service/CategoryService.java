package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.category.CategoryResponse;
import com.shariarunix.refind.dto.category.CreateCategoryRequest;
import com.shariarunix.refind.dto.category.UpdateCategoryRequest;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAllActiveCategories();

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse getCategoryBySlug(String slug);

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);

    CategoryResponse toggleCategoryStatus(Long id);
}
