package com.hana8.hanaro.mapper;

import com.hana8.hanaro.dto.SubscriptionResponseDTO;
import com.hana8.hanaro.entity.Subscription;

public final class SubscriptionMapper {

    private SubscriptionMapper() {
    }

    public static SubscriptionResponseDTO toSubscriptionResponseDTO(Subscription subscription) {
        return new SubscriptionResponseDTO(
                subscription.getId(),
                subscription.getProduct().getName(),
                subscription.getAccount().getAccountNumberFormatted(),
                subscription.getStatus(),
                subscription.getJoinedAt(),
                subscription.getMaturityAt(),
                subscription.getAccumulatedInterest()
        );
    }
}
