package com.hana8.hanaro.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.Role;
import com.hana8.hanaro.dto.SignUpRequestDTO;
import org.junit.jupiter.api.Test;

class AuthMapperTest {

    @Test
    void toUserMapsSignupRequest() {
        SignUpRequestDTO request = new SignUpRequestDTO("user@test.com", "password123", "tester", "01012345678");

        assertThat(AuthMapper.toUser(request, "encoded").getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(AuthMapper.toUser(request, "encoded").getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void toAuthResponseDtoUsesBearerType() {
        assertThat(AuthMapper.toAuthResponseDTO("token").tokenType()).isEqualTo("Bearer");
    }
}
