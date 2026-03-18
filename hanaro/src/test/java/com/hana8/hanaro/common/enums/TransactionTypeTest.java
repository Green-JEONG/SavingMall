package com.hana8.hanaro.common.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TransactionTypeTest {

    @Test
    void containsExpectedValues() {
        assertThat(TransactionType.values()).containsExactly(
                TransactionType.SUBSCRIBE, TransactionType.TRANSFER, TransactionType.TERMINATE
        );
    }
}
