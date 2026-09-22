package com.shariarunix.refind.dto.item;

import com.shariarunix.refind.entity.enums.ItemStatus;
import jakarta.validation.constraints.NotNull;
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
public class UpdateItemStatusRequest {

    @NotNull(message = "Item status is required")
    private ItemStatus status;
}
