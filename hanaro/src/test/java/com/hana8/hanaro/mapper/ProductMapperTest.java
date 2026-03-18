package com.hana8.hanaro.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.SavingsCycle;
import com.hana8.hanaro.dto.ProductRequestDTO;
import com.hana8.hanaro.entity.Product;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductMapperTest {

    @Test
    void toEntityMapsRequestFields() {
        ProductRequestDTO request = new ProductRequestDTO(
                "적금 상품", ProductType.SAVINGS, BigDecimal.valueOf(10000), SavingsCycle.MONTHLY,
                12, BigDecimal.valueOf(4.0), BigDecimal.valueOf(1.5)
        );

        Product product = ProductMapper.toEntity(request, "/upload/image.png");

        assertThat(product.getName()).isEqualTo("적금 상품");
        assertThat(product.getImagePath()).isEqualTo("/upload/image.png");
    }

    @Test
    void toProductResponseDtoMapsEntityFields() {
        Product product = Product.builder()
                .id(1L)
                .name("적금 상품")
                .type(ProductType.SAVINGS)
                .paymentAmount(BigDecimal.valueOf(10000))
                .savingsCycle(SavingsCycle.MONTHLY)
                .periodMonths(12)
                .maturityRate(BigDecimal.valueOf(4.0))
                .terminationRate(BigDecimal.valueOf(1.5))
                .imagePath("/upload/image.png")
                .build();

        assertThat(ProductMapper.toProductResponseDTO(product).id()).isEqualTo(1L);
    }
}
