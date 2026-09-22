package com.shariarunix.refind.dto.item;

import com.shariarunix.refind.entity.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
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
public class CreateItemRequest {

    @NotNull(message = "Item type is required (LOST or FOUND)")
    private ItemType type;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 4000, message = "Description must be between 10 and 4000 characters")
    private String description;

    @Size(max = 255, message = "Location hint must not exceed 255 characters")
    private String specificLocationHint;

    @NotNull(message = "Incident date and time is required")
    @PastOrPresent(message = "Incident date and time cannot be in the future")
    private Instant incidentDateTime;

    @Size(max = 255, message = "Secret question must not exceed 255 characters")
    private String secretIdentifierQuestion;

    @Size(max = 255, message = "Secret answer must not exceed 255 characters")
    private String secretIdentifierAnswer;

    @Size(max = 20, message = "Cannot exceed 20 tags")
    private List<String> tags;

    @Size(max = 5, message = "Cannot attach more than 5 media files")
    private List<Long> mediaIds;

    private Long primaryMediaId;
}
