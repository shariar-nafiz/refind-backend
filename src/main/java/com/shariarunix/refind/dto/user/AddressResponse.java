package com.shariarunix.refind.dto.user;

import com.shariarunix.refind.entity.enums.AddressType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private Long id;
    private AddressType addressType;
    private String country;
    private String state;
    private String city;
    private String streetAddress;
    private String addressLine2;
    private String postalCode;
    private boolean isDefault;
    private Instant createdAt;
    private Instant updatedAt;
}
