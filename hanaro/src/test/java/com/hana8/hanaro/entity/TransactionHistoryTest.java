package com.hana8.hanaro.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class TransactionHistoryTest {

    @Test
    void builderCreatesExpectedEntity() {
        TransactionHistory history = TransactionHistory.builder()
                .subscription(Subscription.builder().build())
                .type(TransactionType.SUBSCRIBE)
                .amount(BigDecimal.valueOf(1000))
                .description("상품 가입")
                .createdAt(LocalDateTime.now())
                .build();

        assertThat(history.getType()).isEqualTo(TransactionType.SUBSCRIBE);
        assertThat(history.getAmount()).isEqualByComparingTo("1000");
        assertThat(history.getDescription()).isEqualTo("상품 가입");
    }
}
