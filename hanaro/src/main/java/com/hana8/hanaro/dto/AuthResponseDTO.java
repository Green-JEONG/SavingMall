package com.hana8.hanaro.dto;

public record AuthResponseDTO(
        String accessToken,
        String tokenType
) {
}
