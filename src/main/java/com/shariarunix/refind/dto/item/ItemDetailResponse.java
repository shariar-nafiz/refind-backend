package com.shariarunix.refind.dto.item;

import com.shariarunix.refind.dto.category.CategoryResponse;
import com.shariarunix.refind.dto.location.LocationResponse;
import com.shariarunix.refind.entity.Item;
import com.shariarunix.refind.entity.enums.ItemStatus;
import com.shariarunix.refind.entity.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDetailResponse {

    private Long id;
    private ItemType type;
    private ItemStatus status;
    private String title;
    private String description;
    private String specificLocationHint;
    private Instant incidentDateTime;

    private CategoryResponse category;
    private LocationResponse location;

    private String secretIdentifierQuestion;
    // NOTE: secretIdentifierAnswer is strictly excluded from public responses

    private List<String> tags;
    private Integer viewCount;

    private List<ItemMediaDto> media;

    private Long posterId;
    private String posterName;
    private String posterAvatarUrl;
    private Boolean isOwner;

    private Instant createdAt;
    private Instant updatedAt;

    public static ItemDetailResponse fromEntity(Item item, Long currentUserId) {
        if (item == null) {
            return null;
        }

        List<ItemMediaDto> mediaDtos = item.getItemMediaList() != null ?
                item.getItemMediaList().stream().map(ItemMediaDto::fromEntity).toList() : List.of();

        boolean owner = currentUserId != null && item.getUser() != null && currentUserId.equals(item.getUser().getId());

        String avatar = null;
        if (item.getUser() != null) {
            avatar = item.getUser().getAvatarUrl();
        }

        return ItemDetailResponse.builder()
                .id(item.getId())
                .type(item.getType())
                .status(item.getStatus())
                .title(item.getTitle())
                .description(item.getDescription())
                .specificLocationHint(item.getSpecificLocationHint())
                .incidentDateTime(item.getIncidentDateTime())
                .category(CategoryResponse.fromEntity(item.getCategory()))
                .location(LocationResponse.fromEntity(item.getLocation()))
                .secretIdentifierQuestion(item.getSecretIdentifierQuestion())
                .tags(item.getTags())
                .viewCount(item.getViewCount())
                .media(mediaDtos)
                .posterId(item.getUser() != null ? item.getUser().getId() : null)
                .posterName(item.getUser() != null ? item.getUser().getFullName() : null)
                .posterAvatarUrl(avatar)
                .isOwner(owner)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
