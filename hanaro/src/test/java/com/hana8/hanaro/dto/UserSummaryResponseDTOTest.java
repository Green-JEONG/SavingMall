package com.hana8.hanaro.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserSummaryResponseDTOTest {

    @Test
    void exposesRecordValues() {
        UserSummaryResponseDTO dto = new UserSummaryResponseDTO(1L, "user@test.com", "nick", "01012345678", "ROLE_USER");

        assertThat(dto.email()).isEqualTo("user@test.com");
        assertThat(dto.role()).isEqualTo("ROLE_USER");
    }
}
