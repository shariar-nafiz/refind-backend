package com.shariarunix.refind.service.impl;

import com.shariarunix.refind.dto.media.MediaResponse;
import com.shariarunix.refind.dto.media.StoredFile;
import com.shariarunix.refind.entity.Media;
import com.shariarunix.refind.entity.User;
import com.shariarunix.refind.repository.MediaRepository;
import com.shariarunix.refind.repository.UserRepository;
import com.shariarunix.refind.service.MediaService;
import com.shariarunix.refind.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final StorageService storageService;
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MediaResponse uploadMedia(MultipartFile file, Long userId, String folder) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        StoredFile storedFile = storageService.store(file, folder);

        Media media = Media.builder()
                .user(user)
                .fileName(storedFile.fileName())
                .fileType(storedFile.fileType())
                .fileSize(storedFile.fileSize())
                .fileUrl(storedFile.fileUrl())
                .storagePath(storedFile.storagePath())
                .build();

        media = mediaRepository.save(media);
        log.info("Saved media metadata with ID: {} for file: {}", media.getId(), media.getFileName());

        return MediaResponse.builder()
                .id(media.getId())
                .fileName(media.getFileName())
                .fileType(media.getFileType())
                .fileSize(media.getFileSize())
                .fileUrl(media.getFileUrl())
                .createdAt(media.getCreatedAt())
                .build();
    }
}
