package com.shariarunix.refind.dto.category;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequest {

    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    @Size(min = 2, max = 100, message = "Slug must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be lowercase alphanumeric with optional single hyphens")
    private String slug;

    @Size(max = 100, message = "Icon name must not exceed 100 characters")
    private String iconName;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    private Integer displayOrder;

    private Boolean isActive;
}
