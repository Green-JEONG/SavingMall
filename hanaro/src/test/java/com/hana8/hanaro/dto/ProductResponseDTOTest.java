package com.hana8.hanaro.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.SavingsCycle;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductResponseDTOTest {

    @Test
    void exposesRecordValues() {
        ProductResponseDTO dto = new ProductResponseDTO(
                1L, "상품", ProductType.SAVINGS, BigDecimal.TEN, SavingsCycle.MONTHLY,
                12, BigDecimal.ONE, BigDecimal.ZERO, "/upload/test.png"
        );

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.imagePath()).isEqualTo("/upload/test.png");
    }
}
