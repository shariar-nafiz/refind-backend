package com.shariarunix.refind.dto.item;

import com.shariarunix.refind.entity.ItemMedia;
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
public class ItemMediaDto {

    private Long mediaId;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private Boolean isPrimary;
    private Integer displayOrder;

    public static ItemMediaDto fromEntity(ItemMedia itemMedia) {
        if (itemMedia == null) {
            return null;
        }
        return ItemMediaDto.builder()
                .mediaId(itemMedia.getMedia() != null ? itemMedia.getMedia().getId() : null)
                .fileUrl(itemMedia.getMedia() != null ? itemMedia.getMedia().getFileUrl() : null)
                .fileName(itemMedia.getMedia() != null ? itemMedia.getMedia().getFileName() : null)
                .fileType(itemMedia.getMedia() != null ? itemMedia.getMedia().getFileType() : null)
                .isPrimary(itemMedia.getIsPrimary())
                .displayOrder(itemMedia.getDisplayOrder())
                .build();
    }
}
