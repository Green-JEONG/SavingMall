package com.hana8.hanaro.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.SavingsCycle;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductRequestDTOTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validatesSavingsCycleConsistency() {
        ProductRequestDTO invalidDeposit = new ProductRequestDTO(
                "예금 상품", ProductType.DEPOSIT, BigDecimal.valueOf(10000), SavingsCycle.MONTHLY,
                12, BigDecimal.valueOf(3.2), BigDecimal.valueOf(1.0)
        );

        assertThat(validator.validate(invalidDeposit)).isNotEmpty();
    }

    @Test
    void acceptsValidSavingsRequest() {
        ProductRequestDTO validSavings = new ProductRequestDTO(
                "적금 상품", ProductType.SAVINGS, BigDecimal.valueOf(10000), SavingsCycle.MONTHLY,
                12, BigDecimal.valueOf(3.2), BigDecimal.valueOf(1.0)
        );

        assertThat(validator.validate(validSavings)).isEmpty();
    }
}
