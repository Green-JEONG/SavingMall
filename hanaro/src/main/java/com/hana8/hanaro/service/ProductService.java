package com.hana8.hanaro.service;

import com.hana8.hanaro.dto.ProductRequest;
import com.hana8.hanaro.dto.ProductResponse;
import com.hana8.hanaro.entity.Product;
import com.hana8.hanaro.repository.ProductRepository;
import com.hana8.hanaro.common.exception.BusinessException;
import com.hana8.hanaro.common.exception.ErrorCode;
import com.hana8.hanaro.common.logging.LogEventPublisher;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    private final LogEventPublisher logEventPublisher;

    @Transactional
    public ProductResponse create(ProductRequest request, MultipartFile image) {
        validateRequest(request);
        String imagePath = fileStorageService.save(image);
        Product product = Product.builder()
                .name(request.name())
                .type(request.type())
                .paymentAmount(request.paymentAmount())
                .savingsCycle(request.savingsCycle())
                .periodMonths(request.periodMonths())
                .maturityRate(request.maturityRate())
                .terminationRate(request.terminationRate())
                .imagePath(imagePath)
                .build();

        Product saved = productRepository.save(product);
        logEventPublisher.product("상품 생성: id=" + saved.getId() + ", name=" + saved.getName());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getOne(Long productId) {
        Product product = findProduct(productId);
        return toResponse(product);
    }

    @Transactional
    public ProductResponse update(Long productId, ProductRequest request, MultipartFile image) {
        validateRequest(request);
        Product product = findProduct(productId);
        String imagePath = image == null || image.isEmpty() ? product.getImagePath() : fileStorageService.save(image);
        product.update(
                request.name(),
                request.type(),
                request.paymentAmount(),
                request.savingsCycle(),
                request.periodMonths(),
                request.maturityRate(),
                request.terminationRate(),
                imagePath
        );
        logEventPublisher.product("상품 수정: id=" + product.getId() + ", name=" + product.getName());
        return toResponse(product);
    }

    @Transactional
    public void delete(Long productId) {
        Product product = findProduct(productId);
        productRepository.delete(product);
        logEventPublisher.product("상품 삭제: id=" + productId);
    }

    public Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private void validateRequest(ProductRequest request) {
        boolean invalidDeposit = request.type() == com.hana8.hanaro.common.enums.ProductType.DEPOSIT
                && request.savingsCycle() != null;
        boolean invalidSavings = request.type() == com.hana8.hanaro.common.enums.ProductType.SAVINGS
                && request.savingsCycle() == null;
        if (invalidDeposit || invalidSavings) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_REQUEST);
        }
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
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
