package com.shariarunix.refind.exception;

import com.shariarunix.refind.dto.common.ApiResponse;
import com.shariarunix.refind.dto.common.ApiSubError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UserAccountDisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAccountDisabled(UserAccountDisabledException ex, HttpServletRequest request) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(org.springframework.security.authentication.DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabledException(org.springframework.security.authentication.DisabledException ex, HttpServletRequest request) {
        ApiResponse<Void> response = ApiResponse.error("This account is pending email verification. Please verify your email before logging in.", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        ApiResponse<Void> response = ApiResponse.error("Invalid email/phone or password", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        ApiResponse<Void> response = ApiResponse.error("Access is denied", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ApiSubError> subErrors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            subErrors.add(ApiSubError.builder()
                    .field(fieldError.getField())
                    .rejectedValue(fieldError.getRejectedValue())
                    .message(fieldError.getDefaultMessage())
                    .build());
        }

        ApiResponse<Void> response = ApiResponse.error("Validation failed for one or more fields", subErrors, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex, HttpServletRequest request) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "An unexpected internal server error occurred";
        ApiResponse<Void> response = ApiResponse.error(msg, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
