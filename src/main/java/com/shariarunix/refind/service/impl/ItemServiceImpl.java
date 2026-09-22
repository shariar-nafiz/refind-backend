package com.shariarunix.refind.service.impl;

import com.shariarunix.refind.dto.item.CreateItemRequest;
import com.shariarunix.refind.dto.item.ItemDetailResponse;
import com.shariarunix.refind.dto.item.ItemSearchCriteria;
import com.shariarunix.refind.dto.item.ItemSummaryResponse;
import com.shariarunix.refind.dto.item.UpdateItemRequest;
import com.shariarunix.refind.dto.item.UpdateItemStatusRequest;
import com.shariarunix.refind.entity.Category;
import com.shariarunix.refind.entity.Item;
import com.shariarunix.refind.entity.ItemMedia;
import com.shariarunix.refind.entity.Location;
import com.shariarunix.refind.entity.Media;
import com.shariarunix.refind.entity.User;
import com.shariarunix.refind.entity.enums.ItemStatus;
import com.shariarunix.refind.entity.enums.ItemType;
import com.shariarunix.refind.event.ItemCreatedEvent;
import com.shariarunix.refind.exception.BadRequestException;
import com.shariarunix.refind.exception.ResourceNotFoundException;
import com.shariarunix.refind.repository.CategoryRepository;
import com.shariarunix.refind.repository.ItemMediaRepository;
import com.shariarunix.refind.repository.ItemRepository;
import com.shariarunix.refind.repository.LocationRepository;
import com.shariarunix.refind.repository.MediaRepository;
import com.shariarunix.refind.repository.UserRepository;
import com.shariarunix.refind.repository.specification.ItemSpecification;
import com.shariarunix.refind.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;
    private final ItemMediaRepository itemMediaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ItemDetailResponse createItem(Long currentUserId, CreateItemRequest request) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + currentUserId));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));
        if (!Boolean.TRUE.equals(category.getIsActive())) {
            throw new BadRequestException("Selected category is currently inactive");
        }

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + request.getLocationId()));
        if (!Boolean.TRUE.equals(location.getIsActive())) {
            throw new BadRequestException("Selected location is currently inactive");
        }

        // Validate secret question and answer for FOUND items
        String secretQuestion = request.getSecretIdentifierQuestion();
        String secretAnswer = request.getSecretIdentifierAnswer();
        if (request.getType() == ItemType.FOUND && secretQuestion != null && !secretQuestion.isBlank()) {
            if (secretAnswer == null || secretAnswer.isBlank()) {
                throw new BadRequestException("Secret identifier answer is required when setting a secret verification question");
            }
        }

        List<String> cleanedTags = new ArrayList<>();
        if (request.getTags() != null) {
            cleanedTags = request.getTags().stream()
                    .filter(t -> t != null && !t.isBlank())
                    .map(t -> t.trim().toLowerCase())
                    .distinct()
                    .toList();
        }

        Item item = Item.builder()
                .user(user)
                .category(category)
                .location(location)
                .type(request.getType())
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .specificLocationHint(request.getSpecificLocationHint() != null ? request.getSpecificLocationHint().trim() : null)
                .incidentDateTime(request.getIncidentDateTime())
                .secretIdentifierQuestion(secretQuestion != null ? secretQuestion.trim() : null)
                .secretIdentifierAnswer(secretAnswer != null ? secretAnswer.trim() : null)
                .tags(cleanedTags)
                .status(ItemStatus.OPEN)
                .viewCount(0)
                .build();

        // Handle attached media
        if (request.getMediaIds() != null && !request.getMediaIds().isEmpty()) {
            Long primaryMediaId = request.getPrimaryMediaId() != null ?
                    request.getPrimaryMediaId() : request.getMediaIds().get(0);

            for (int i = 0; i < request.getMediaIds().size(); i++) {
                Long mediaId = request.getMediaIds().get(i);
                Media media = mediaRepository.findById(mediaId)
                        .orElseThrow(() -> new ResourceNotFoundException("Media not found with ID: " + mediaId));
                boolean isPrimary = mediaId.equals(primaryMediaId);
                item.addMedia(media, isPrimary, i);
            }
        }

        Item saved = itemRepository.save(item);
        log.info("Created item report: id={}, type={}, title='{}', user={}",
                saved.getId(), saved.getType(), saved.getTitle(), user.getEmail());

        // Publish event for downstream matching engine
        eventPublisher.publishEvent(new ItemCreatedEvent(this, saved.getId(), saved.getType()));

        return ItemDetailResponse.fromEntity(saved, currentUserId);
    }

    @Override
    @Transactional
    public ItemDetailResponse getItemById(Long id, Long currentUserId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item report not found with ID: " + id));

        // Increment view count
        itemRepository.incrementViewCount(id);

        return ItemDetailResponse.fromEntity(item, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> searchItems(ItemSearchCriteria criteria) {
        Specification<Item> spec = ItemSpecification.withCriteria(criteria);
        Page<Item> page = itemRepository.findAll(spec, criteria.toPageable());
        return page.map(ItemSummaryResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> getMyItems(Long currentUserId, ItemType type, ItemStatus status, Pageable pageable) {
        Page<Item> page;
        if (type != null) {
            page = itemRepository.findByUserIdAndType(currentUserId, type, pageable);
        } else if (status != null) {
            page = itemRepository.findByUserIdAndStatus(currentUserId, status, pageable);
        } else {
            page = itemRepository.findByUserId(currentUserId, pageable);
        }
        return page.map(ItemSummaryResponse::fromEntity);
    }

    @Override
    @Transactional
    public ItemDetailResponse updateItem(Long id, Long currentUserId, UpdateItemRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item report not found with ID: " + id));

        if (!item.getUser().getId().equals(currentUserId)) {
            throw new BadRequestException("You do not have permission to update this item report");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            item.setTitle(request.getTitle().trim());
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            item.setDescription(request.getDescription().trim());
        }

        if (request.getCategoryId() != null && !request.getCategoryId().equals(item.getCategory().getId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));
            item.setCategory(category);
        }

        if (request.getLocationId() != null && !request.getLocationId().equals(item.getLocation().getId())) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + request.getLocationId()));
            item.setLocation(location);
        }

        if (request.getSpecificLocationHint() != null) {
            item.setSpecificLocationHint(request.getSpecificLocationHint().trim());
        }

        if (request.getIncidentDateTime() != null) {
            item.setIncidentDateTime(request.getIncidentDateTime());
        }

        if (request.getSecretIdentifierQuestion() != null) {
            item.setSecretIdentifierQuestion(request.getSecretIdentifierQuestion().trim());
        }

        if (request.getSecretIdentifierAnswer() != null) {
            item.setSecretIdentifierAnswer(request.getSecretIdentifierAnswer().trim());
        }

        if (request.getTags() != null) {
            List<String> cleanedTags = request.getTags().stream()
                    .filter(t -> t != null && !t.isBlank())
                    .map(t -> t.trim().toLowerCase())
                    .distinct()
                    .toList();
            item.setTags(cleanedTags);
        }

        // Update media attachments if specified
        if (request.getMediaIds() != null) {
            item.getItemMediaList().clear();
            Long primaryMediaId = request.getPrimaryMediaId() != null ?
                    request.getPrimaryMediaId() :
                    (request.getMediaIds().isEmpty() ? null : request.getMediaIds().get(0));

            for (int i = 0; i < request.getMediaIds().size(); i++) {
                Long mediaId = request.getMediaIds().get(i);
                Media media = mediaRepository.findById(mediaId)
                        .orElseThrow(() -> new ResourceNotFoundException("Media not found with ID: " + mediaId));
                boolean isPrimary = mediaId.equals(primaryMediaId);
                item.addMedia(media, isPrimary, i);
            }
        }

        Item updated = itemRepository.save(item);
        log.info("Updated item report: id={}", updated.getId());
        return ItemDetailResponse.fromEntity(updated, currentUserId);
    }

    @Override
    @Transactional
    public ItemDetailResponse updateItemStatus(Long id, Long currentUserId, UpdateItemStatusRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item report not found with ID: " + id));

        if (!item.getUser().getId().equals(currentUserId)) {
            throw new BadRequestException("You do not have permission to alter the status of this item");
        }

        item.setStatus(request.getStatus());
        Item updated = itemRepository.save(item);
        log.info("Updated item status: id={}, status={}", updated.getId(), updated.getStatus());
        return ItemDetailResponse.fromEntity(updated, currentUserId);
    }

    @Override
    @Transactional
    public void deleteItem(Long id, Long currentUserId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item report not found with ID: " + id));

        if (!item.getUser().getId().equals(currentUserId)) {
            throw new BadRequestException("You do not have permission to delete this item report");
        }

        itemRepository.delete(item);
        log.info("Deleted item report: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemSummaryResponse> getRecentItems(ItemType type, int limit) {
        return itemRepository.findTop10ByTypeAndStatusOrderByCreatedAtDesc(type, ItemStatus.OPEN).stream()
                .limit(limit > 0 ? limit : 10)
                .map(ItemSummaryResponse::fromEntity)
                .toList();
    }
}
