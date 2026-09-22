package com.shariarunix.refind.dto.admin;

import com.shariarunix.refind.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class UpdateUserRoleRequest {

    @Schema(description = "New authorization role", example = "ROLE_ADMIN")
    @NotNull(message = "Role cannot be null")
    private Role role;
}
