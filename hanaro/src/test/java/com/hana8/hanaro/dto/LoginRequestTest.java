package com.hana8.hanaro.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class LoginRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validatesRequiredFields() {
        LoginRequest request = new LoginRequest("", "");

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
