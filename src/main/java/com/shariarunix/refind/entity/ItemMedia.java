package com.shariarunix.refind.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
        name = "item_media",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_item_media_item_media", columnNames = {"item_id", "media_id"})
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemMedia extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @Builder.Default
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @Builder.Default
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;
}
