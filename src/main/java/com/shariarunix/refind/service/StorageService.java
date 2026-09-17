package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.media.StoredFile;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    StoredFile store(MultipartFile file, String subDirectory);

    void delete(String storagePath);
}
