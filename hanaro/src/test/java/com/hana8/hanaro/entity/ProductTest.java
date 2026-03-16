package com.hana8.hanaro.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.SavingsCycle;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void givenProduct_whenUpdate_thenFieldsAreChanged() {
        Product product = Product.builder()
                .name("기존 상품")
                .type(ProductType.DEPOSIT)
                .paymentAmount(BigDecimal.valueOf(1000000))
                .savingsCycle(null)
                .periodMonths(12)
                .maturityRate(BigDecimal.valueOf(3.20))
                .terminationRate(BigDecimal.valueOf(1.20))
                .imagePath("/upload/old.png")
                .build();

        product.update(
                "변경 상품",
                ProductType.SAVINGS,
                BigDecimal.valueOf(300000),
                SavingsCycle.MONTHLY,
                24,
                BigDecimal.valueOf(4.10),
                BigDecimal.valueOf(1.80),
                "/upload/new.png"
        );

        assertThat(product.getName()).isEqualTo("변경 상품");
        assertThat(product.getType()).isEqualTo(ProductType.SAVINGS);
        assertThat(product.getPaymentAmount()).isEqualByComparingTo("300000");
        assertThat(product.getSavingsCycle()).isEqualTo(SavingsCycle.MONTHLY);
        assertThat(product.getPeriodMonths()).isEqualTo(24);
        assertThat(product.getMaturityRate()).isEqualByComparingTo("4.10");
        assertThat(product.getTerminationRate()).isEqualByComparingTo("1.80");
        assertThat(product.getImagePath()).isEqualTo("/upload/new.png");
    }
}
