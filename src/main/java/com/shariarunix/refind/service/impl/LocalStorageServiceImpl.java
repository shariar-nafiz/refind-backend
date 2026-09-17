package com.shariarunix.refind.service.impl;

import com.shariarunix.refind.dto.media.StoredFile;
import com.shariarunix.refind.exception.BadRequestException;
import com.shariarunix.refind.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class LocalStorageServiceImpl implements StorageService {

    private final Path baseStorageLocation;
    private final List<String> allowedMimeTypes;

    public LocalStorageServiceImpl(
            @Value("${storage.local.base-dir:./uploads}") String baseDir,
            @Value("${storage.allowed-formats:image/jpeg,image/png,image/webp}") String allowedFormats
    ) {
        this.baseStorageLocation = Paths.get(baseDir).toAbsolutePath().normalize();
        this.allowedMimeTypes = Arrays.stream(allowedFormats.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.baseStorageLocation);
            log.info("Initialized local storage at: {}", this.baseStorageLocation);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create base upload directory: " + this.baseStorageLocation, e);
        }
    }

    @Override
    public StoredFile store(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Failed to store empty file");
        }

        String rawContentType = file.getContentType();
        String contentType = rawContentType != null ? rawContentType.toLowerCase().trim() : "";
        if (!allowedMimeTypes.contains(contentType)) {
            throw new BadRequestException("File type '" + rawContentType + "' is not supported. Allowed formats: " + allowedMimeTypes);
        }

        String rawFilename = file.getOriginalFilename();
        String originalFilename = rawFilename != null ? StringUtils.cleanPath(rawFilename) : "file";
        if (originalFilename.contains("..")) {
            throw new BadRequestException("Cannot store file with relative path sequence outside current directory: " + originalFilename);
        }

        String extension = getFileExtension(originalFilename);
        String uniqueFileName = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);

        String cleanSubDirectory = StringUtils.hasText(subDirectory) ? sanitizeDirectory(subDirectory) : "misc";
        Path targetDirectory = this.baseStorageLocation.resolve(cleanSubDirectory).normalize();

        if (!targetDirectory.startsWith(this.baseStorageLocation)) {
            throw new BadRequestException("Invalid target directory path");
        }

        try {
            Files.createDirectories(targetDirectory);
            Path destinationFile = targetDirectory.resolve(uniqueFileName).normalize();

            if (!destinationFile.startsWith(targetDirectory)) {
                throw new BadRequestException("Cannot store file outside specified directory");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            String storagePath = destinationFile.toString();
            String fileUrl = "/uploads/" + cleanSubDirectory + "/" + uniqueFileName;

            log.info("Successfully stored file '{}' at '{}'", uniqueFileName, destinationFile);
            return new StoredFile(originalFilename, contentType, file.getSize(), storagePath, fileUrl);
        } catch (IOException e) {
            log.error("Failed to store file: {}", originalFilename, e);
            throw new RuntimeException("Failed to store file: " + originalFilename, e);
        }
    }

    @Override
    public void delete(String storagePath) {
        if (!StringUtils.hasText(storagePath)) {
            return;
        }
        try {
            Path file = Paths.get(storagePath).toAbsolutePath().normalize();
            if (file.startsWith(this.baseStorageLocation)) {
                Files.deleteIfExists(file);
                log.info("Deleted stored file: {}", file);
            } else {
                log.warn("Refused to delete file outside storage directory: {}", storagePath);
            }
        } catch (IOException e) {
            log.warn("Could not delete file at storage path: {}", storagePath, e);
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    private String sanitizeDirectory(String subDirectory) {
        return subDirectory.replaceAll("[^a-zA-Z0-9_-]", "").toLowerCase();
    }
}
