package com.hana8.hanaro.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class TransferRequestDTOTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validatesSubscriptionAmountAndAccountNumber() {
        TransferRequestDTO request = new TransferRequestDTO(null, BigDecimal.ZERO, "12-345");

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
