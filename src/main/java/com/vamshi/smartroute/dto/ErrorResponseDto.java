package com.vamshi.smartroute.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(
    LocalDateTime timestamp,
    int status,
    String code,
    String message,
    String path
) {
    public ErrorResponseDto(int status, String code, String message, String path) {
        this(LocalDateTime.now(), status, code, message, path);
    }
}
