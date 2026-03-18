package com.hana8.hanaro.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.Role;
import com.hana8.hanaro.entity.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class UserMapperTest {

    @Test
    void mapsUserToSummaryDto() {
        User user = User.builder()
                .id(1L)
                .email("user@test.com")
                .password("password")
                .nickname("tester")
                .phoneNumber("01012345678")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();

        assertThat(UserMapper.toUserSummaryResponseDTO(user).email()).isEqualTo("user@test.com");
    }
}
