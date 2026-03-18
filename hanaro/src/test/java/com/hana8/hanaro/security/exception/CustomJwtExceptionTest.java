package com.hana8.hanaro.security.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CustomJwtExceptionTest {

    @Test
    void storesMessage() {
        CustomJwtException exception = new CustomJwtException("token error");

        assertThat(exception).hasMessage("token error");
    }
}
