package com.hana8.hanaro.controller;

import com.hana8.hanaro.dto.ProductResponseDTO;
import com.hana8.hanaro.service.ProductService;
import com.hana8.hanaro.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
@Tag(name = "사용자 상품 API", description = "일반 사용자 상품 목록 조회 및 상세 조회 API입니다.")
public class UserProductController {

    private final ProductService productService;

    @Operation(summary = "상품 목록", description = "예금 및 적금 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 목록 조회에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "USER 또는 ADMIN 권한이 필요합니다.")
    })
    @GetMapping
    public ApiResponse<List<ProductResponseDTO>> getAll() {
        return ApiResponse.ok(productService.getAll());
    }

    @Operation(summary = "상품 상세", description = "상품 ID로 상세 상품 정보를 조회합니다.")
    @Parameters({
            @Parameter(name = "productId", description = "상품 ID", example = "1")
    })
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 상세 조회에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "USER 또는 ADMIN 권한이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 상품이 없습니다.")
    })
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDTO> getOne(@PathVariable Long productId) {
        return ApiResponse.ok(productService.getOne(productId));
    }
}
