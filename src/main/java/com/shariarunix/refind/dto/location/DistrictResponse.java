package com.shariarunix.refind.dto.location;

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
public class DistrictResponse {

    private String division;
    private String district;
    private Long thanaCount;
}
