package com.shariarunix.refind.controller;

import com.shariarunix.refind.dto.common.ApiResponse;
import com.shariarunix.refind.dto.item.CreateItemRequest;
import com.shariarunix.refind.dto.item.ItemDetailResponse;
import com.shariarunix.refind.dto.item.ItemSearchCriteria;
import com.shariarunix.refind.dto.item.ItemSummaryResponse;
import com.shariarunix.refind.dto.item.UpdateItemRequest;
import com.shariarunix.refind.dto.item.UpdateItemStatusRequest;
import com.shariarunix.refind.entity.enums.ItemStatus;
import com.shariarunix.refind.entity.enums.ItemType;
import com.shariarunix.refind.security.UserPrincipal;
import com.shariarunix.refind.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@Tag(name = "Items", description = "Endpoints for creating, discovering, managing, and resolving lost and found item reports")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @Operation(summary = "Report a lost or found item", description = "Creates a new item report. When reporting a found item, a secret verification question and answer can be supplied.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<ItemDetailResponse>> createItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateItemRequest request,
            HttpServletRequest httpRequest
    ) {
        ItemDetailResponse item = itemService.createItem(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item report created successfully", item, httpRequest.getRequestURI()));
    }

    @GetMapping
    @Operation(summary = "Search and filter items", description = "Public directory search for lost and found items with multi-faceted filtering and pagination.")
    public ResponseEntity<ApiResponse<List<ItemSummaryResponse>>> searchItems(
            @ModelAttribute ItemSearchCriteria criteria,
            HttpServletRequest httpRequest
    ) {
        Page<ItemSummaryResponse> page = itemService.searchItems(criteria);
        return ResponseEntity.ok(ApiResponse.paginated("Items retrieved successfully", page, httpRequest.getRequestURI()));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent items", description = "Public feed of recently reported open items for landing page widgets.")
    public ResponseEntity<ApiResponse<List<ItemSummaryResponse>>> getRecentItems(
            @RequestParam(name = "type", defaultValue = "LOST") ItemType type,
            @RequestParam(name = "limit", defaultValue = "6") int limit,
            HttpServletRequest httpRequest
    ) {
        List<ItemSummaryResponse> items = itemService.getRecentItems(type, limit);
        return ResponseEntity.ok(ApiResponse.success("Recent items retrieved successfully", items, httpRequest.getRequestURI()));
    }

    @GetMapping("/my-items")
    @Operation(summary = "Get authenticated user's items", description = "Retrieves all lost and found item reports submitted by the current authenticated user.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<ItemSummaryResponse>>> getMyItems(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(name = "type", required = false) ItemType type,
            @RequestParam(name = "status", required = false) ItemStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            HttpServletRequest httpRequest
    ) {
        Page<ItemSummaryResponse> myItems = itemService.getMyItems(
                userPrincipal.getId(),
                type,
                status,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        return ResponseEntity.ok(ApiResponse.paginated("User items retrieved successfully", myItems, httpRequest.getRequestURI()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item details by ID", description = "Retrieves complete details of an item report. Confidential verification answer is always masked.")
    public ResponseEntity<ApiResponse<ItemDetailResponse>> getItemById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletRequest httpRequest
    ) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : null;
        ItemDetailResponse item = itemService.getItemById(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Item details retrieved successfully", item, httpRequest.getRequestURI()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update item details", description = "Updates details of an existing item report. Only the original author can edit.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<ItemDetailResponse>> updateItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateItemRequest request,
            HttpServletRequest httpRequest
    ) {
        ItemDetailResponse updated = itemService.updateItem(id, userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Item updated successfully", updated, httpRequest.getRequestURI()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update item status", description = "Changes lifecycle status of an item report (e.g., mark as RESOLVED or ARCHIVED).")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<ItemDetailResponse>> updateItemStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateItemStatusRequest request,
            HttpServletRequest httpRequest
    ) {
        ItemDetailResponse updated = itemService.updateItemStatus(id, userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Item status updated successfully", updated, httpRequest.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item report", description = "Permanently removes an item report. Only author can delete.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletRequest httpRequest
    ) {
        itemService.deleteItem(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Item report deleted successfully", httpRequest.getRequestURI()));
    }
}
