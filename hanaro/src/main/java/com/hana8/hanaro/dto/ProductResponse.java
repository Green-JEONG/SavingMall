package com.hana8.hanaro.dto;

import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.SavingsCycle;
import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        ProductType type,
        BigDecimal paymentAmount,
        SavingsCycle savingsCycle,
        Integer periodMonths,
        BigDecimal maturityRate,
        BigDecimal terminationRate,
        String imagePath
) {
}
