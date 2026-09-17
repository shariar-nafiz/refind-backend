package com.shariarunix.refind.dto.media;

public record StoredFile(
        String fileName,
        String fileType,
        long fileSize,
        String storagePath,
        String fileUrl
) {
}
