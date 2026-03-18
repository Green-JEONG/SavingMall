package com.hana8.hanaro.common.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RoleTest {

    @Test
    void containsExpectedValues() {
        assertThat(Role.values()).containsExactly(Role.ROLE_ADMIN, Role.ROLE_USER);
    }
}
