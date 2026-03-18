package com.hana8.hanaro.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.Role;
import com.hana8.hanaro.common.enums.SubscriptionStatus;
import com.hana8.hanaro.entity.Account;
import com.hana8.hanaro.entity.Product;
import com.hana8.hanaro.entity.Subscription;
import com.hana8.hanaro.entity.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class SubscriptionMapperTest {

    @Test
    void mapsSubscriptionToResponseDto() {
        Subscription subscription = Subscription.builder()
                .id(1L)
                .user(User.builder().email("user@test.com").role(Role.ROLE_USER).createdAt(LocalDateTime.now()).build())
                .product(Product.builder().name("예금").type(ProductType.DEPOSIT).paymentAmount(BigDecimal.TEN)
                        .periodMonths(12).maturityRate(BigDecimal.ONE).terminationRate(BigDecimal.ZERO).build())
                .account(Account.builder().accountNumberFormatted("123-4567-8901").build())
                .joinedAt(LocalDate.now())
                .maturityAt(LocalDate.now().plusMonths(12))
                .status(SubscriptionStatus.ACTIVE)
                .accumulatedInterest(BigDecimal.ZERO)
                .build();

        assertThat(SubscriptionMapper.toSubscriptionResponseDTO(subscription).accountNumber())
                .isEqualTo("123-4567-8901");
    }
}
