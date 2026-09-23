package com.vamshi.smartroute.dto;

public record AuthResponseDto(
    String token,
    String tokenType,
    String username,
    String role
) {
    public AuthResponseDto(String token, String username, String role) {
        this(token, "Bearer", username, role);
    }
}
