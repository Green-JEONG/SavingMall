package com.hana8.hanaro.common.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SubscriptionStatusTest {

    @Test
    void containsExpectedValues() {
        assertThat(SubscriptionStatus.values()).containsExactly(
                SubscriptionStatus.ACTIVE, SubscriptionStatus.MATURED, SubscriptionStatus.TERMINATED
        );
    }
}
