package com.hana8.hanaro.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.SubscriptionStatus;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SubscriptionTest {

    @Test
    void givenActiveSubscription_whenTerminate_thenStatusAndInterestAreUpdated() {
        Subscription subscription = Subscription.builder()
                .status(SubscriptionStatus.ACTIVE)
                .accumulatedInterest(BigDecimal.ZERO)
                .build();

        subscription.terminate(BigDecimal.valueOf(1234.56));

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.TERMINATED);
        assertThat(subscription.getAccumulatedInterest()).isEqualByComparingTo("1234.56");
    }

    @Test
    void givenActiveSubscription_whenMature_thenStatusAndInterestAreUpdated() {
        Subscription subscription = Subscription.builder()
                .status(SubscriptionStatus.ACTIVE)
                .accumulatedInterest(BigDecimal.ZERO)
                .build();

        subscription.mature(BigDecimal.valueOf(987.65));

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.MATURED);
        assertThat(subscription.getAccumulatedInterest()).isEqualByComparingTo("987.65");
    }
}
