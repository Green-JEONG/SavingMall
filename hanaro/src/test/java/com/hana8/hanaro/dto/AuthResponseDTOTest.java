package com.hana8.hanaro.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AuthResponseDTOTest {

    @Test
    void exposesRecordValues() {
        AuthResponseDTO dto = new AuthResponseDTO("token", "Bearer");

        assertThat(dto.accessToken()).isEqualTo("token");
        assertThat(dto.tokenType()).isEqualTo("Bearer");
    }
}
