package com.shariarunix.refind.dto.user;

import com.shariarunix.refind.entity.enums.AddressType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class AddressRequest {

    @Schema(description = "Type of address", example = "HOME")
    @Builder.Default
    private AddressType addressType = AddressType.HOME;

    @Schema(description = "Country name or ISO code", example = "Bangladesh")
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Schema(description = "State, province, division, or region", example = "Dhaka")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Schema(description = "City, town, or municipality", example = "Dhaka")
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Schema(description = "Primary street address or building details", example = "House 12, Road 4, Sector 3")
    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address must not exceed 255 characters")
    private String streetAddress;

    @Schema(description = "Secondary address line (apartment, suite, unit)", example = "Apt 4B")
    @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
    private String addressLine2;

    @Schema(description = "Postal or ZIP code", example = "1230")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @Schema(description = "Set as default primary address", example = "true")
    @Builder.Default
    private Boolean isDefault = true;
}
