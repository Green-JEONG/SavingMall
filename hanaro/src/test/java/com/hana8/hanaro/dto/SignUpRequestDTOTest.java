package com.hana8.hanaro.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class SignUpRequestDTOTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validatesEmailPasswordNicknameAndPhoneNumber() {
        SignUpRequestDTO request = new SignUpRequestDTO("bad", "1234", "", "010");

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
