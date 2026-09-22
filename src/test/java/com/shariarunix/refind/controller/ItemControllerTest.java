package com.shariarunix.refind.controller;

import com.shariarunix.refind.dto.item.ItemDetailResponse;
import com.shariarunix.refind.dto.item.ItemSearchCriteria;
import com.shariarunix.refind.dto.item.ItemSummaryResponse;
import com.shariarunix.refind.entity.enums.ItemStatus;
import com.shariarunix.refind.entity.enums.ItemType;
import com.shariarunix.refind.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private ItemDetailResponse sampleDetail;
    private ItemSummaryResponse sampleSummary;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        sampleDetail = ItemDetailResponse.builder()
                .id(100L)
                .type(ItemType.LOST)
                .status(ItemStatus.OPEN)
                .title("Black Leather Wallet")
                .description("Lost around Dhanmondi Lake")
                .secretIdentifierQuestion("What color is the inner zip?")
                .viewCount(12)
                .createdAt(Instant.now())
                .build();

        sampleSummary = ItemSummaryResponse.builder()
                .id(100L)
                .type(ItemType.LOST)
                .status(ItemStatus.OPEN)
                .title("Black Leather Wallet")
                .categoryName("Wallets & Purses")
                .district("Dhaka")
                .thana("Dhanmondi")
                .viewCount(12)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/items/{id} returns item detail")
    void getItemById_Success() throws Exception {
        when(itemService.getItemById(eq(100L), any())).thenReturn(sampleDetail);

        mockMvc.perform(get("/api/v1/items/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.title").value("Black Leather Wallet"))
                .andExpect(jsonPath("$.data.secretIdentifierQuestion").value("What color is the inner zip?"));
    }

    @Test
    @DisplayName("GET /api/v1/items returns paginated items")
    void searchItems_Success() throws Exception {
        when(itemService.searchItems(any(ItemSearchCriteria.class)))
                .thenReturn(new PageImpl<>(List.of(sampleSummary)));

        mockMvc.perform(get("/api/v1/items")
                        .param("query", "wallet")
                        .param("type", "LOST")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(100))
                .andExpect(jsonPath("$.data[0].title").value("Black Leather Wallet"));
    }

    @Test
    @DisplayName("GET /api/v1/items/recent returns list of recent items")
    void getRecentItems_Success() throws Exception {
        when(itemService.getRecentItems(eq(ItemType.LOST), eq(6)))
                .thenReturn(List.of(sampleSummary));

        mockMvc.perform(get("/api/v1/items/recent")
                        .param("type", "LOST")
                        .param("limit", "6")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Black Leather Wallet"));
    }
}
