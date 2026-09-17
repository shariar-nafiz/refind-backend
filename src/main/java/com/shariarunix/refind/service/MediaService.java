package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.media.MediaResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    MediaResponse uploadMedia(MultipartFile file, Long userId, String folder);
}
