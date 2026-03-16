package com.hana8.hanaro.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.entity.Product;
import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.SavingsCycle;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void saveAndFind() {
        Product saved = productRepository.save(Product.builder()
                .name("테스트")
                .type(ProductType.SAVINGS)
                .paymentAmount(BigDecimal.valueOf(10000))
                .savingsCycle(SavingsCycle.MONTHLY)
                .periodMonths(12)
                .maturityRate(BigDecimal.valueOf(3.2))
                .terminationRate(BigDecimal.valueOf(1.5))
                .build());

        assertThat(productRepository.findById(saved.getId())).isPresent();
        assertThat(productRepository.findAll()).hasSize(1);
    }
}
