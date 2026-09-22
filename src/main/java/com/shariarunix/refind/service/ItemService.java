package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.item.CreateItemRequest;
import com.shariarunix.refind.dto.item.ItemDetailResponse;
import com.shariarunix.refind.dto.item.ItemSearchCriteria;
import com.shariarunix.refind.dto.item.ItemSummaryResponse;
import com.shariarunix.refind.dto.item.UpdateItemRequest;
import com.shariarunix.refind.dto.item.UpdateItemStatusRequest;
import com.shariarunix.refind.entity.enums.ItemStatus;
import com.shariarunix.refind.entity.enums.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {

    ItemDetailResponse createItem(Long currentUserId, CreateItemRequest request);

    ItemDetailResponse getItemById(Long id, Long currentUserId);

    Page<ItemSummaryResponse> searchItems(ItemSearchCriteria criteria);

    Page<ItemSummaryResponse> getMyItems(Long currentUserId, ItemType type, ItemStatus status, Pageable pageable);

    ItemDetailResponse updateItem(Long id, Long currentUserId, UpdateItemRequest request);

    ItemDetailResponse updateItemStatus(Long id, Long currentUserId, UpdateItemStatusRequest request);

    void deleteItem(Long id, Long currentUserId);

    List<ItemSummaryResponse> getRecentItems(ItemType type, int limit);
}
