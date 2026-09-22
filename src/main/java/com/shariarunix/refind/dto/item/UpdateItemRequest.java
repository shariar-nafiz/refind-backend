package com.shariarunix.refind.dto.item;

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
public class UpdateItemRequest {

    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Size(min = 10, max = 4000, message = "Description must be between 10 and 4000 characters")
    private String description;

    private Long categoryId;

    private Long locationId;

    @Size(max = 255, message = "Location hint must not exceed 255 characters")
    private String specificLocationHint;

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
