package com.hana8.hanaro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.SavingsCycle;
import com.hana8.hanaro.dto.ProductRequestDTO;
import com.hana8.hanaro.dto.ProductResponseDTO;
import com.hana8.hanaro.entity.Product;
import com.hana8.hanaro.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private FileService fileService;

    @InjectMocks
    private ProductService productService;

    @Test
    void givenRequestAndImage_whenCreate_thenSavedProductIsReturned() {
        ProductRequestDTO request = productRequest();
        MockMultipartFile image = new MockMultipartFile("image", "sample.png", "image/png", "img".getBytes());
        given(fileService.upload(image)).willReturn("/upload/sample.png");
        given(productRepository.save(any(Product.class))).willAnswer(invocation -> {
            Product source = invocation.getArgument(0);
            return Product.builder()
                    .id(1L)
                    .name(source.getName())
                    .type(source.getType())
                    .paymentAmount(source.getPaymentAmount())
                    .savingsCycle(source.getSavingsCycle())
                    .periodMonths(source.getPeriodMonths())
                    .maturityRate(source.getMaturityRate())
                    .terminationRate(source.getTerminationRate())
                    .imagePath(source.getImagePath())
                    .build();
        });

        ProductResponseDTO response = productService.create(request, image);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("적금 상품");
        assertThat(response.imagePath()).isEqualTo("/upload/sample.png");
    }

    @Test
    void givenExistingProductAndNoImage_whenUpdate_thenExistingImagePathIsPreserved() {
        Product product = Product.builder()
                .id(1L)
                .name("기존")
                .type(ProductType.SAVINGS)
                .paymentAmount(BigDecimal.valueOf(100000))
                .savingsCycle(SavingsCycle.MONTHLY)
                .periodMonths(12)
                .maturityRate(BigDecimal.valueOf(4.00))
                .terminationRate(BigDecimal.valueOf(1.20))
                .imagePath("/upload/original.png")
                .build();
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        ProductResponseDTO response = productService.update(1L, productRequest(), null);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.imagePath()).isEqualTo("/upload/original.png");
        assertThat(product.getName()).isEqualTo("적금 상품");
        verify(fileService, never()).upload(any());
    }

    @Test
    void givenMissingProduct_whenDelete_thenProductNotFoundExceptionIsThrown() {
        given(productRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void givenDepositRequestWithSavingsCycle_whenCreate_thenInvalidProductRequestExceptionIsThrown() {
        ProductRequestDTO request = new ProductRequestDTO(
                "예금 상품",
                ProductType.DEPOSIT,
                BigDecimal.valueOf(500000),
                SavingsCycle.MONTHLY,
                12,
                BigDecimal.valueOf(3.20),
                BigDecimal.valueOf(1.10)
        );

        assertThatThrownBy(() -> productService.create(request, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 입력값이 상품 유형과 일치하지 않습니다.");
    }

    private ProductRequestDTO productRequest() {
        return new ProductRequestDTO(
                "적금 상품",
                ProductType.SAVINGS,
                BigDecimal.valueOf(200000),
                SavingsCycle.MONTHLY,
                24,
                BigDecimal.valueOf(4.50),
                BigDecimal.valueOf(1.80)
        );
    }
}
