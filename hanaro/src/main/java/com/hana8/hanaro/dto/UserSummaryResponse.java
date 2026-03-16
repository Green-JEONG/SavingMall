package com.hana8.hanaro.dto;

public record UserSummaryResponse(
        Long id,
        String email,
        String nickname,
        String phoneNumber,
        String role
) {
}
