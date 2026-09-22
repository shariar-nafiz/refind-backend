package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.category.CategoryResponse;
import com.shariarunix.refind.dto.category.CreateCategoryRequest;
import com.shariarunix.refind.dto.category.UpdateCategoryRequest;
import com.shariarunix.refind.entity.Category;
import com.shariarunix.refind.exception.BadRequestException;
import com.shariarunix.refind.exception.ResourceNotFoundException;
import com.shariarunix.refind.repository.CategoryRepository;
import com.shariarunix.refind.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .name("Electronics")
                .slug("electronics")
                .iconName("laptop")
                .description("Electronic gadgets and accessories")
                .isActive(true)
                .displayOrder(1)
                .build();
        testCategory.setId(1L);
    }

    @Test
    @DisplayName("getAllActiveCategories returns list of active categories")
    void getAllActiveCategories_Success() {
        when(categoryRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc())
                .thenReturn(List.of(testCategory));

        List<CategoryResponse> result = categoryService.getAllActiveCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Electronics");
        assertThat(result.get(0).getSlug()).isEqualTo("electronics");
    }

    @Test
    @DisplayName("getCategoryById returns category response when found")
    void getCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        CategoryResponse result = categoryService.getCategoryById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("getCategoryById throws ResourceNotFoundException when not found")
    void getCategoryById_NotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    @DisplayName("createCategory saves and returns new category when valid")
    void createCategory_Success() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Documents")
                .slug("documents")
                .iconName("file")
                .description("Official identification documents")
                .displayOrder(2)
                .build();

        when(categoryRepository.existsByNameIgnoreCase("Documents")).thenReturn(false);
        when(categoryRepository.existsBySlug("documents")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        CategoryResponse result = categoryService.createCategory(request);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Documents");
        assertThat(result.getSlug()).isEqualTo("documents");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("createCategory throws BadRequestException when category name duplicate")
    void createCategory_DuplicateName() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Electronics")
                .slug("electronics-2")
                .build();

        when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Category already exists with name");
    }

    @Test
    @DisplayName("toggleCategoryStatus switches active boolean")
    void toggleCategoryStatus_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        CategoryResponse result = categoryService.toggleCategoryStatus(1L);

        assertThat(result.getIsActive()).isFalse();
        verify(categoryRepository).save(testCategory);
    }
}
