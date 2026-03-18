package com.hana8.hanaro.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.SubscriptionStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SubscriptionTest {

    @Test
    void terminateUpdatesStatusAndInterest() {
        Subscription subscription = subscription();

        subscription.terminate(BigDecimal.valueOf(100));

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.TERMINATED);
        assertThat(subscription.getAccumulatedInterest()).isEqualByComparingTo("100");
    }

    @Test
    void matureUpdatesStatusAndInterest() {
        Subscription subscription = subscription();

        subscription.mature(BigDecimal.valueOf(200));

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.MATURED);
        assertThat(subscription.getAccumulatedInterest()).isEqualByComparingTo("200");
    }

    private Subscription subscription() {
        return Subscription.builder()
                .user(User.builder().build())
                .product(Product.builder().build())
                .account(Account.builder().build())
                .joinedAt(LocalDate.now())
                .maturityAt(LocalDate.now().plusMonths(12))
                .status(SubscriptionStatus.ACTIVE)
                .accumulatedInterest(BigDecimal.ZERO)
                .build();
    }
}
