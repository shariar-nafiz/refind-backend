package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.media.MediaResponse;
import com.shariarunix.refind.dto.media.StoredFile;
import com.shariarunix.refind.entity.Media;
import com.shariarunix.refind.entity.User;
import com.shariarunix.refind.repository.MediaRepository;
import com.shariarunix.refind.repository.UserRepository;
import com.shariarunix.refind.service.impl.MediaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaServiceImplTest {

    @Mock
    private StorageService storageService;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MediaServiceImpl mediaService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .email("user@example.com")
                .fullName("Test User")
                .build();
        sampleUser.setId(10L);
    }

    @Test
    void uploadMedia_Success() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "dummy image".getBytes()
        );

        StoredFile storedFile = new StoredFile(
                "photo.jpg",
                "image/jpeg",
                11L,
                "/tmp/uploads/items/uuid_photo.jpg",
                "/uploads/items/uuid_photo.jpg"
        );

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(storageService.store(eq(file), eq("items"))).thenReturn(storedFile);

        Media savedMedia = Media.builder()
                .user(sampleUser)
                .fileName(storedFile.fileName())
                .fileType(storedFile.fileType())
                .fileSize(storedFile.fileSize())
                .fileUrl(storedFile.fileUrl())
                .storagePath(storedFile.storagePath())
                .build();
        savedMedia.setId(100L);
        savedMedia.setCreatedAt(Instant.now());

        when(mediaRepository.save(any(Media.class))).thenReturn(savedMedia);

        MediaResponse response = mediaService.uploadMedia(file, 10L, "items");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getFileName()).isEqualTo("photo.jpg");
        assertThat(response.getFileUrl()).isEqualTo("/uploads/items/uuid_photo.jpg");

        verify(storageService).store(file, "items");
        verify(mediaRepository).save(any(Media.class));
    }
}
