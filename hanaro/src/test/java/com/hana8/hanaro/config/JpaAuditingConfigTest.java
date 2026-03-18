package com.hana8.hanaro.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JpaAuditingConfigTest {

    @Test
    void canBeInstantiated() {
        assertThat(new JpaAuditingConfig()).isNotNull();
    }
}
