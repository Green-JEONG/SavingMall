package com.hana8.hanaro.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class SubscribeRequestDTOTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validatesProductIdAndAccountNumber() {
        SubscribeRequestDTO request = new SubscribeRequestDTO(null, "1234");

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
