package com.shariarunix.refind.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "locations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_locations_district_thana", columnNames = {"district", "thana"})
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location extends BaseEntity {

    @Column(name = "division", nullable = false, length = 100)
    private String division;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "thana", nullable = false, length = 100)
    private String thana;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
