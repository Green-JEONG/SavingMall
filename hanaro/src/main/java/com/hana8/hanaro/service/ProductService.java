package com.hana8.hanaro.service;

import com.hana8.hanaro.dto.ProductRequestDTO;
import com.hana8.hanaro.dto.ProductResponseDTO;
import com.hana8.hanaro.entity.Product;
import com.hana8.hanaro.mapper.ProductMapper;
import com.hana8.hanaro.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger PRODUCT_LOGGER = LoggerFactory.getLogger("audit.product");

    private final ProductRepository productRepository;
    private final FileService fileService;

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO request, MultipartFile image) {
        validateRequest(request);
        String imagePath = fileService.upload(image);
        Product product = ProductMapper.toEntity(request, imagePath);

        Product saved = productRepository.save(product);
        PRODUCT_LOGGER.info("상품 생성: id={}, name={}", saved.getId(), saved.getName());
        return ProductMapper.toProductResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAll() {
        return productRepository.findAll().stream().map(ProductMapper::toProductResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getOne(Long productId) {
        Product product = findProduct(productId);
        return ProductMapper.toProductResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO update(Long productId, ProductRequestDTO request, MultipartFile image) {
        validateRequest(request);
        Product product = findProduct(productId);
        String imagePath = image == null || image.isEmpty() ? product.getImagePath() : fileService.upload(image);
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
        PRODUCT_LOGGER.info("상품 수정: id={}, name={}", product.getId(), product.getName());
        return ProductMapper.toProductResponseDTO(product);
    }

    @Transactional
    public void delete(Long productId) {
        Product product = findProduct(productId);
        productRepository.delete(product);
        PRODUCT_LOGGER.info("상품 삭제: id={}", productId);
    }

    public Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));
    }

    private void validateRequest(ProductRequestDTO request) {
        boolean invalidDeposit = request.type() == com.hana8.hanaro.common.enums.ProductType.DEPOSIT
                && request.savingsCycle() != null;
        boolean invalidSavings = request.type() == com.hana8.hanaro.common.enums.ProductType.SAVINGS
                && request.savingsCycle() == null;
        if (invalidDeposit || invalidSavings) {
            throw new IllegalArgumentException("예금은 납입 주기를 비워야 하고, 적금은 납입 주기를 선택해야 합니다.");
        }
    }

}
