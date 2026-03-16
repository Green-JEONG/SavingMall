package com.hana8.hanaro.dto;

import com.hana8.hanaro.common.validator.AccountNumber;
import jakarta.validation.constraints.NotNull;

public record SubscribeRequest(
        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,

        @AccountNumber
        String accountNumber
) {
}
