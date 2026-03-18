package com.hana8.hanaro.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.SavingsCycle;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void updateChangesAllMutableFields() {
        Product product = Product.builder()
                .name("old")
                .type(ProductType.SAVINGS)
                .paymentAmount(BigDecimal.ONE)
                .savingsCycle(SavingsCycle.MONTHLY)
                .periodMonths(12)
                .maturityRate(BigDecimal.ONE)
                .terminationRate(BigDecimal.ZERO)
                .imagePath("/upload/old.png")
                .build();

        product.update("new", ProductType.DEPOSIT, BigDecimal.TEN, null, 6,
                BigDecimal.valueOf(2.5), BigDecimal.valueOf(0.5), "/upload/new.png");

        assertThat(product.getName()).isEqualTo("new");
        assertThat(product.getType()).isEqualTo(ProductType.DEPOSIT);
        assertThat(product.getSavingsCycle()).isNull();
        assertThat(product.getImagePath()).isEqualTo("/upload/new.png");
    }
}
