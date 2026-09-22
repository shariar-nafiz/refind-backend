package com.shariarunix.refind.dto.item;

import com.shariarunix.refind.entity.enums.ItemStatus;
import com.shariarunix.refind.entity.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchCriteria {

    private String query;
    private ItemType type;
    private Long categoryId;
    private String division;
    private String district;
    private String thana;
    private ItemStatus status;
    private Instant dateFrom;
    private Instant dateTo;

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDirection = "DESC";

    public Pageable toPageable() {
        int pageNum = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0 && size <= 50) ? size : 20;

        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String field = switch (sortBy != null ? sortBy.toLowerCase() : "") {
            case "incidentdatetime", "incident_date_time" -> "incidentDateTime";
            case "viewcount", "view_count" -> "viewCount";
            case "title" -> "title";
            default -> "createdAt";
        };

        return PageRequest.of(pageNum, pageSize, Sort.by(direction, field));
    }
}
