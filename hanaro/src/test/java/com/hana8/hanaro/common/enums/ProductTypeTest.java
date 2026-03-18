package com.hana8.hanaro.common.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProductTypeTest {

    @Test
    void containsExpectedValues() {
        assertThat(ProductType.values()).containsExactly(ProductType.DEPOSIT, ProductType.SAVINGS);
    }
}
