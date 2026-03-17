package com.hana8.hanaro.mapper;

import com.hana8.hanaro.dto.ProductRequestDTO;
import com.hana8.hanaro.dto.ProductResponseDTO;
import com.hana8.hanaro.entity.Product;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static Product toEntity(ProductRequestDTO request, String imagePath) {
        return Product.builder()
                .name(request.name())
                .type(request.type())
                .paymentAmount(request.paymentAmount())
                .savingsCycle(request.savingsCycle())
                .periodMonths(request.periodMonths())
                .maturityRate(request.maturityRate())
                .terminationRate(request.terminationRate())
                .imagePath(imagePath)
                .build();
    }

    public static ProductResponseDTO toProductResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getType(),
                product.getPaymentAmount(),
                product.getSavingsCycle(),
                product.getPeriodMonths(),
                product.getMaturityRate(),
                product.getTerminationRate(),
                product.getImagePath()
        );
    }
}
