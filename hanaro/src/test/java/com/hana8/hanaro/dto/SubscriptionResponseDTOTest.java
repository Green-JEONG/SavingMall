package com.hana8.hanaro.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.SubscriptionStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SubscriptionResponseDTOTest {

    @Test
    void exposesRecordValues() {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO(
                1L, "상품", "123-4567-8901", SubscriptionStatus.ACTIVE,
                LocalDate.now(), LocalDate.now().plusMonths(12), BigDecimal.ZERO
        );

        assertThat(dto.productName()).isEqualTo("상품");
        assertThat(dto.accountNumber()).isEqualTo("123-4567-8901");
    }
}
