package com.hana8.hanaro.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.Role;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void syncAdminUpdatesRolePasswordAndNickname() {
        User user = User.builder()
                .email("user@test.com")
                .password("old")
                .nickname("oldNick")
                .phoneNumber("01012345678")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();

        user.syncAdmin("encoded", "adminNick");

        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(user.getNickname()).isEqualTo("adminNick");
        assertThat(user.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }
}
