package com.hana8.hanaro.controller;

import com.hana8.hanaro.dto.ProductRequestDTO;
import com.hana8.hanaro.dto.ProductResponseDTO;
import com.hana8.hanaro.service.ProductService;
import com.hana8.hanaro.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;

    @Operation(summary = "관리자 상품 목록 조회")
    @GetMapping
    public ApiResponse<List<ProductResponseDTO>> getAll() {
        return ApiResponse.ok(productService.getAll());
    }

    @Operation(summary = "관리자 상품 등록")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponseDTO> create(
            @Valid @RequestPart("request") ProductRequestDTO request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ApiResponse.ok(productService.create(request, image));
    }

    @Operation(summary = "관리자 상품 조회")
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDTO> getOne(@PathVariable Long productId) {
        return ApiResponse.ok(productService.getOne(productId));
    }

    @Operation(summary = "관리자 상품 수정")
    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponseDTO> update(
            @PathVariable Long productId,
            @Valid @RequestPart("request") ProductRequestDTO request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ApiResponse.ok(productService.update(productId, request, image));
    }

    @Operation(summary = "관리자 상품 삭제")
    @DeleteMapping("/{productId}")
    public ApiResponse<Void> delete(@PathVariable Long productId) {
        productService.delete(productId);
        return ApiResponse.okMessage("상품 삭제가 완료되었습니다.");
    }
}
