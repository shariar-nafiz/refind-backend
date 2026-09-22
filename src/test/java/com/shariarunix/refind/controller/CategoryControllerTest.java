package com.shariarunix.refind.controller;

import com.shariarunix.refind.dto.category.CategoryResponse;
import com.shariarunix.refind.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryResponse sampleCategory;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();

        sampleCategory = CategoryResponse.builder()
                .id(1L)
                .name("Wallets & Purses")
                .slug("wallets-purses")
                .iconName("wallet")
                .description("Wallets, coin purses, card holders")
                .isActive(true)
                .displayOrder(1)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/categories returns list of active categories")
    void getAllActiveCategories_Success() throws Exception {
        when(categoryService.getAllActiveCategories()).thenReturn(List.of(sampleCategory));

        mockMvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Wallets & Purses"))
                .andExpect(jsonPath("$.data[0].slug").value("wallets-purses"));
    }

    @Test
    @DisplayName("GET /api/v1/categories/{id} returns single category")
    void getCategoryById_Success() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(sampleCategory);

        mockMvc.perform(get("/api/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.slug").value("wallets-purses"));
    }

    @Test
    @DisplayName("GET /api/v1/categories/slug/{slug} returns category by slug")
    void getCategoryBySlug_Success() throws Exception {
        when(categoryService.getCategoryBySlug("wallets-purses")).thenReturn(sampleCategory);

        mockMvc.perform(get("/api/v1/categories/slug/wallets-purses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Wallets & Purses"));
    }
}
