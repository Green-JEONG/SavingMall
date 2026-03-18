package com.hana8.hanaro.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.hana8.hanaro.common.response.ApiResponse;
import com.hana8.hanaro.dto.ProductRequestDTO;
import com.hana8.hanaro.dto.ProductResponseDTO;
import com.hana8.hanaro.service.ProductService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
    @PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminProductController {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().findAndAddModules().build();
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private final ProductService productService;

    @Operation(summary = "관리자 상품 목록 조회")
    @GetMapping
    public ApiResponse<List<ProductResponseDTO>> getAll() {
        return ApiResponse.ok(productService.getAll());
    }

    @Operation(summary = "관리자 상품 등록")
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = ProductMultipartRequestDocs.class)
            )
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponseDTO> create(
            @RequestParam(value = "image", required = false) MultipartFile image,
            @Parameter(hidden = true)
            HttpServletRequest servletRequest
    ) {
        String requestJson = resolveRequestJson(servletRequest);
        ProductRequestDTO request = parseAndValidate(requestJson);
        return ApiResponse.ok(productService.create(request, image));
    }

    @Operation(summary = "관리자 상품 조회")
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDTO> getOne(@PathVariable Long productId) {
        return ApiResponse.ok(productService.getOne(productId));
    }

    @Operation(summary = "관리자 상품 수정")
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = ProductMultipartRequestDocs.class)
            )
    )
    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponseDTO> update(
            @PathVariable Long productId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @Parameter(hidden = true)
            HttpServletRequest servletRequest
    ) {
        String requestJson = resolveRequestJson(servletRequest);
        ProductRequestDTO request = parseAndValidate(requestJson);
        return ApiResponse.ok(productService.update(productId, request, image));
    }

    @Operation(summary = "관리자 상품 삭제")
    @DeleteMapping("/{productId}")
    public ApiResponse<Void> delete(@PathVariable Long productId) {
        productService.delete(productId);
        return ApiResponse.okMessage("상품 삭제가 완료되었습니다.");
    }

    private ProductRequestDTO parseAndValidate(String requestJson) {
        ProductRequestDTO request = readRequest(requestJson);
        Set<ConstraintViolation<ProductRequestDTO>> violations = VALIDATOR.validate(request);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(violations.iterator().next().getMessage());
        }
        return request;
    }

    private String resolveRequestJson(HttpServletRequest servletRequest) {
        String requestJson = servletRequest.getParameter("request");
        if (requestJson != null && !requestJson.isBlank()) {
            return requestJson;
        }

        if (servletRequest instanceof MultipartHttpServletRequest multipartRequest) {
            MultipartFile requestFile = multipartRequest.getFile("request");
            if (requestFile != null && !requestFile.isEmpty()) {
                try {
                    String body = new String(requestFile.getBytes(), StandardCharsets.UTF_8);
                    if (!body.isBlank()) {
                        return body;
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("상품 요청 정보를 읽을 수 없습니다.");
                }
            }
        }

        try {
            Part requestPart = servletRequest.getPart("request");
            if (requestPart == null) {
                throw new IllegalArgumentException("상품 요청 정보는 필수입니다.");
            }

            byte[] bytes = requestPart.getInputStream().readAllBytes();
            String body = new String(bytes, StandardCharsets.UTF_8);
            if (body.isBlank()) {
                throw new IllegalArgumentException("상품 요청 정보는 필수입니다.");
            }
            return body;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("상품 요청 정보를 읽을 수 없습니다.");
        }
    }

    private ProductRequestDTO readRequest(String requestJson) {
        try {
            return OBJECT_MAPPER.readValue(requestJson, ProductRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("상품 요청 정보가 올바른 JSON 형식이 아닙니다.");
        }
    }

    @Schema(name = "ProductMultipartRequestDocs", requiredProperties = {"request"})
    private static class ProductMultipartRequestDocs {
        @Schema(
                description = "상품 정보",
                implementation = ProductRequestDTO.class
        )
        public ProductRequestDTO request;

        @Schema(type = "string", format = "binary", description = "상품 이미지 파일")
        public String image;
    }
}
