package com.shariarunix.refind.controller;

import com.shariarunix.refind.dto.common.ApiResponse;
import com.shariarunix.refind.dto.media.MediaResponse;
import com.shariarunix.refind.security.UserPrincipal;
import com.shariarunix.refind.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Endpoints for uploading and storing media files")
@SecurityRequirement(name = "bearerAuth")
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload media file", description = "Uploads an image file (JPEG, PNG, WebP) to local storage and persists media metadata.")
    public ResponseEntity<ApiResponse<MediaResponse>> uploadMedia(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "items") String folder,
            HttpServletRequest httpRequest
    ) {
        Long userId = userPrincipal != null ? userPrincipal.getId() : null;
        MediaResponse response = mediaService.uploadMedia(file, userId, folder);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Media uploaded successfully", response, httpRequest.getRequestURI()));
    }
}
