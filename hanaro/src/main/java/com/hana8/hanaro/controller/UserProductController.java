package com.hana8.hanaro.controller;

import com.hana8.hanaro.dto.ProductResponseDTO;
import com.hana8.hanaro.service.ProductService;
import com.hana8.hanaro.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/products")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class UserProductController {

    private final ProductService productService;

    @Operation(summary = "상품 목록")
    @GetMapping
    public ApiResponse<List<ProductResponseDTO>> getAll() {
        return ApiResponse.ok(productService.getAll());
    }

    @Operation(summary = "상품 상세")
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDTO> getOne(@PathVariable Long productId) {
        return ApiResponse.ok(productService.getOne(productId));
    }
}
