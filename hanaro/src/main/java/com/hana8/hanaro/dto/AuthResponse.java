package com.hana8.hanaro.dto;

public record AuthResponse(
        String accessToken,
        String tokenType
) {
}
