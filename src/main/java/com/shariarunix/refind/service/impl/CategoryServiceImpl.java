package com.shariarunix.refind.service.impl;

import com.shariarunix.refind.dto.category.CategoryResponse;
import com.shariarunix.refind.dto.category.CreateCategoryRequest;
import com.shariarunix.refind.dto.category.UpdateCategoryRequest;
import com.shariarunix.refind.entity.Category;
import com.shariarunix.refind.exception.BadRequestException;
import com.shariarunix.refind.exception.ResourceNotFoundException;
import com.shariarunix.refind.repository.CategoryRepository;
import com.shariarunix.refind.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllActiveCategories() {
        return categoryRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return CategoryResponse.fromEntity(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return CategoryResponse.fromEntity(category);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Category already exists with name: " + request.getName());
        }
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new BadRequestException("Category already exists with slug: " + request.getSlug());
        }

        Category category = Category.builder()
                .name(request.getName().trim())
                .slug(request.getSlug().trim().toLowerCase())
                .iconName(request.getIconName())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isActive(true)
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Created new category: id={}, name={}, slug={}", saved.getId(), saved.getName(), saved.getSlug());
        return CategoryResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        if (request.getName() != null && !request.getName().trim().equalsIgnoreCase(category.getName())) {
            if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
                throw new BadRequestException("Category already exists with name: " + request.getName());
            }
            category.setName(request.getName().trim());
        }

        if (request.getSlug() != null && !request.getSlug().trim().equalsIgnoreCase(category.getSlug())) {
            if (categoryRepository.existsBySlug(request.getSlug().trim().toLowerCase())) {
                throw new BadRequestException("Category already exists with slug: " + request.getSlug());
            }
            category.setSlug(request.getSlug().trim().toLowerCase());
        }

        if (request.getIconName() != null) {
            category.setIconName(request.getIconName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getDisplayOrder() != null) {
            category.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }

        Category updated = categoryRepository.save(category);
        log.info("Updated category: id={}, name={}", updated.getId(), updated.getName());
        return CategoryResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public CategoryResponse toggleCategoryStatus(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        category.setIsActive(!Boolean.TRUE.equals(category.getIsActive()));
        Category updated = categoryRepository.save(category);
        log.info("Toggled category status: id={}, isActive={}", updated.getId(), updated.getIsActive());
        return CategoryResponse.fromEntity(updated);
    }
}
