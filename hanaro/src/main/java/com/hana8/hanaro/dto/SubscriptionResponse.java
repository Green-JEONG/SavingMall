package com.hana8.hanaro.dto;

import com.hana8.hanaro.common.enums.SubscriptionStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SubscriptionResponse(
        Long id,
        String productName,
        String accountNumber,
        SubscriptionStatus status,
        LocalDate joinedAt,
        LocalDate maturityAt,
        BigDecimal accumulatedInterest
) {
}
