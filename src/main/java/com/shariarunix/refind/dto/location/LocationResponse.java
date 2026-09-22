package com.shariarunix.refind.dto.location;

import com.shariarunix.refind.entity.Location;
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
public class LocationResponse {

    private Long id;
    private String division;
    private String district;
    private String thana;
    private Boolean isActive;

    public static LocationResponse fromEntity(Location location) {
        if (location == null) {
            return null;
        }
        return LocationResponse.builder()
                .id(location.getId())
                .division(location.getDivision())
                .district(location.getDistrict())
                .thana(location.getThana())
                .isActive(location.getIsActive())
                .build();
    }
}
