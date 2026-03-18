package com.hana8.hanaro.dto;

import com.hana8.hanaro.common.validator.AccountNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubscribeRequestDTO(
        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,

        @NotBlank(message = "계좌번호는 필수입니다.")
        @AccountNumber
        String accountNumber
) {
}
