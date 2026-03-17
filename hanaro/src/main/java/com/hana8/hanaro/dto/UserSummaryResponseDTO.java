package com.hana8.hanaro.dto;

public record UserSummaryResponseDTO(
        Long id,
        String email,
        String nickname,
        String phoneNumber,
        String role
) {
}
