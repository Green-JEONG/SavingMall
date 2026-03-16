package com.hana8.hanaro.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.entity.TransactionHistory;
import com.hana8.hanaro.common.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class TransactionHistoryRepositoryTest {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Test
    void saveAndFind() {
        transactionHistoryRepository.save(TransactionHistory.builder()
                .subscription(null)
                .type(TransactionType.SUBSCRIBE)
                .amount(BigDecimal.valueOf(1000))
                .description("테스트")
                .createdAt(LocalDateTime.now())
                .build());

        assertThat(transactionHistoryRepository.findAll()).hasSize(1);
    }
}
