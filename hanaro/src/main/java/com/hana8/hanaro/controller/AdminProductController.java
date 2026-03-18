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
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "관리자 상품 API", description = "관리자의 예금/적금 상품 등록, 조회, 수정, 삭제 API입니다.")
public class AdminProductController {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().findAndAddModules().build();
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private final ProductService productService;

    @Operation(summary = "관리자 상품 목록 조회", description = "등록된 전체 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 목록 조회에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.")
    })
    @GetMapping
    public ApiResponse<List<ProductResponseDTO>> getAll() {
        return ApiResponse.ok(productService.getAll());
    }

    @Operation(summary = "관리자 상품 등록", description = "상품 정보와 대표 이미지 1개를 함께 등록합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 등록에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "상품 요청 정보가 없거나 형식이 올바르지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.")
    })
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = ProductMultipartRequestDocs.class)
            )
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponseDTO> create(
            @Parameter(name = "image", description = "대표 이미지 파일 1개")
            @RequestParam(value = "image", required = false) MultipartFile image,
            @Parameter(hidden = true)
            HttpServletRequest servletRequest
    ) {
        String requestJson = resolveRequestJson(servletRequest);
        ProductRequestDTO request = parseAndValidate(requestJson);
        return ApiResponse.ok(productService.create(request, image));
    }

    @Operation(summary = "관리자 상품 조회", description = "상품 ID로 특정 상품 정보를 조회합니다.")
    @Parameters({
            @Parameter(name = "productId", description = "상품 ID", example = "1")
    })
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 조회에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 상품이 없습니다.")
    })
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDTO> getOne(@PathVariable Long productId) {
        return ApiResponse.ok(productService.getOne(productId));
    }

    @Operation(summary = "관리자 상품 수정", description = "상품 ID에 해당하는 상품 정보를 수정하고 대표 이미지 1개를 교체할 수 있습니다.")
    @Parameters({
            @Parameter(name = "productId", description = "상품 ID", example = "1")
    })
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 수정에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "상품 요청 정보가 없거나 형식이 올바르지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "수정할 상품이 없습니다.")
    })
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
            @Parameter(name = "image", description = "대표 이미지 파일 1개")
            @RequestParam(value = "image", required = false) MultipartFile image,
            @Parameter(hidden = true)
            HttpServletRequest servletRequest
    ) {
        String requestJson = resolveRequestJson(servletRequest);
        ProductRequestDTO request = parseAndValidate(requestJson);
        return ApiResponse.ok(productService.update(productId, request, image));
    }

    @Operation(summary = "관리자 상품 삭제", description = "상품 ID에 해당하는 상품을 삭제합니다.")
    @Parameters({
            @Parameter(name = "productId", description = "상품 ID", example = "1")
    })
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 삭제에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "삭제할 상품이 없습니다.")
    })
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

        @Schema(type = "string", format = "binary", description = "대표 이미지 파일 1개")
        public String image;
    }
}
