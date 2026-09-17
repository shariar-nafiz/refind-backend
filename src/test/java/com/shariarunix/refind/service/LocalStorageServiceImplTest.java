package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.media.StoredFile;
import com.shariarunix.refind.exception.BadRequestException;
import com.shariarunix.refind.service.impl.LocalStorageServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private LocalStorageServiceImpl storageService;

    @BeforeEach
    void setUp() {
        storageService = new LocalStorageServiceImpl(
                tempDir.toString(),
                "image/jpeg,image/png,image/webp"
        );
        storageService.init();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    void store_ValidPngFile_Success() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                "fake image content".getBytes()
        );

        StoredFile stored = storageService.store(file, "avatars");

        assertThat(stored).isNotNull();
        assertThat(stored.fileName()).isEqualTo("avatar.png");
        assertThat(stored.fileType()).isEqualTo("image/png");
        assertThat(stored.fileSize()).isGreaterThan(0);
        assertThat(stored.fileUrl()).startsWith("/uploads/avatars/");
        assertThat(Files.exists(Path.of(stored.storagePath()))).isTrue();
    }

    @Test
    void store_EmptyFile_ThrowsBadRequest() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        assertThatThrownBy(() -> storageService.store(file, "items"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("empty file");
    }

    @Test
    void store_UnsupportedFormat_ThrowsBadRequest() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "script.sh",
                "application/x-sh",
                "echo hello".getBytes()
        );

        assertThatThrownBy(() -> storageService.store(file, "items"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not supported");
    }

    @Test
    void store_PathTraversal_ThrowsBadRequest() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "../../etc/passwd.png",
                "image/png",
                "test".getBytes()
        );

        assertThatThrownBy(() -> storageService.store(file, "items"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("relative path sequence");
    }

    @Test
    void delete_StoredFile_RemovesFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.webp",
                "image/webp",
                "image data".getBytes()
        );

        StoredFile stored = storageService.store(file, "items");
        assertThat(Files.exists(Path.of(stored.storagePath()))).isTrue();

        storageService.delete(stored.storagePath());
        assertThat(Files.exists(Path.of(stored.storagePath()))).isFalse();
    }
}
