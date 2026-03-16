package com.hana8.hanaro.dto;

import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.SavingsCycle;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "상품명은 필수입니다.")
        @Size(max = 100, message = "상품명은 100자를 초과할 수 없습니다.")
        String name,

        @NotNull(message = "상품 종류는 필수입니다.")
        ProductType type,

        @NotNull(message = "납입 금액은 필수입니다.")
        @Positive(message = "납입 금액은 0보다 커야 합니다.")
        BigDecimal paymentAmount,

        SavingsCycle savingsCycle,

        @NotNull(message = "가입 기간은 필수입니다.")
        @Positive(message = "가입 기간은 1개월 이상이어야 합니다.")
        Integer periodMonths,

        @NotNull(message = "만기 수익률은 필수입니다.")
        @DecimalMin(value = "0.00", message = "만기 수익률은 0 이상이어야 합니다.")
        @DecimalMax(value = "100.00", message = "만기 수익률은 100 이하여야 합니다.")
        BigDecimal maturityRate,

        @NotNull(message = "해지 수익률은 필수입니다.")
        @DecimalMin(value = "0.00", message = "해지 수익률은 0 이상이어야 합니다.")
        @DecimalMax(value = "100.00", message = "해지 수익률은 100 이하여야 합니다.")
        BigDecimal terminationRate
) {
}
