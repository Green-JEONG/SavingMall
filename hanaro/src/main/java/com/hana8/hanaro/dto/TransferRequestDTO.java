package com.hana8.hanaro.dto;

import com.hana8.hanaro.common.validator.AccountNumber;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransferRequestDTO(
        @NotNull(message = "가입 ID는 필수입니다.")
        Long subscriptionId,

        @NotNull(message = "이체 금액은 필수입니다.")
        @DecimalMin(value = "1.00", message = "이체 금액은 1 이상이어야 합니다.")
        BigDecimal amount,

        @NotBlank(message = "출금 계좌번호는 필수입니다.")
        @AccountNumber
        String fromAccountNumber
) {
}
