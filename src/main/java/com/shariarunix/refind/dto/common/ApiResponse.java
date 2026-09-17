package com.shariarunix.refind.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private PaginationMeta pagination;
    private List<ApiSubError> errors;

    @Builder.Default
    private Instant timestamp = Instant.now();

    private String path;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Operation completed successfully")
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data, String path) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Void> ok(String message, String path) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<List<T>> paginated(String message, Page<T> page, String path) {
        return ApiResponse.<List<T>>builder()
                .success(true)
                .message(message)
                .data(page != null ? page.getContent() : List.of())
                .pagination(PaginationMeta.fromPage(page))
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, List<ApiSubError> errors, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }
}
