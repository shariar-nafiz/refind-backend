package com.shariarunix.refind.dto.item;

import com.shariarunix.refind.entity.Item;
import com.shariarunix.refind.entity.ItemMedia;
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
public class ItemSummaryResponse {

    private Long id;
    private ItemType type;
    private ItemStatus status;
    private String title;
    private String descriptionSnippet;

    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private String categoryIcon;

    private Long locationId;
    private String division;
    private String district;
    private String thana;
    private String specificLocationHint;

    private Instant incidentDateTime;
    private String primaryImageUrl;
    private List<String> tags;
    private Boolean hasSecretQuestion;
    private Integer viewCount;
    private Instant createdAt;

    public static ItemSummaryResponse fromEntity(Item item) {
        if (item == null) {
            return null;
        }

        String primaryImageUrl = null;
        if (item.getItemMediaList() != null && !item.getItemMediaList().isEmpty()) {
            primaryImageUrl = item.getItemMediaList().stream()
                    .filter(im -> Boolean.TRUE.equals(im.getIsPrimary()))
                    .findFirst()
                    .map(im -> im.getMedia().getFileUrl())
                    .orElseGet(() -> item.getItemMediaList().get(0).getMedia().getFileUrl());
        }

        String desc = item.getDescription();
        String snippet = (desc != null && desc.length() > 140) ? desc.substring(0, 137) + "..." : desc;

        return ItemSummaryResponse.builder()
                .id(item.getId())
                .type(item.getType())
                .status(item.getStatus())
                .title(item.getTitle())
                .descriptionSnippet(snippet)
                .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .categorySlug(item.getCategory() != null ? item.getCategory().getSlug() : null)
                .categoryIcon(item.getCategory() != null ? item.getCategory().getIconName() : null)
                .locationId(item.getLocation() != null ? item.getLocation().getId() : null)
                .division(item.getLocation() != null ? item.getLocation().getDivision() : null)
                .district(item.getLocation() != null ? item.getLocation().getDistrict() : null)
                .thana(item.getLocation() != null ? item.getLocation().getThana() : null)
                .specificLocationHint(item.getSpecificLocationHint())
                .incidentDateTime(item.getIncidentDateTime())
                .primaryImageUrl(primaryImageUrl)
                .tags(item.getTags())
                .hasSecretQuestion(item.getSecretIdentifierQuestion() != null && !item.getSecretIdentifierQuestion().isBlank())
                .viewCount(item.getViewCount())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
