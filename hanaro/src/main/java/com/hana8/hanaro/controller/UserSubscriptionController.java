package com.hana8.hanaro.controller;

import com.hana8.hanaro.dto.SubscribeRequestDTO;
import com.hana8.hanaro.dto.SubscriptionResponseDTO;
import com.hana8.hanaro.dto.TransferRequestDTO;
import com.hana8.hanaro.service.SubscriptionService;
import com.hana8.hanaro.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/subscriptions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
@Tag(name = "사용자 가입 API", description = "상품 가입, 내 가입 내역 조회, 중도 해지, 계좌 이체 API입니다.")
public class UserSubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "상품 가입", description = "선택한 상품에 가입하고 희망 계좌번호를 직접 입력하거나 자동 생성할 수 있습니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 가입에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증에 실패했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "USER 또는 ADMIN 권한이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 상품이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 사용 중인 계좌번호입니다.")
    })
    @PostMapping
    public ApiResponse<SubscriptionResponseDTO> subscribe(@Valid @RequestBody SubscribeRequestDTO request) {
        return ApiResponse.ok(subscriptionService.subscribe(request));
    }

    @Operation(summary = "내 가입 내역", description = "로그인한 사용자의 전체 가입 내역과 현재 누적 이자를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "가입 내역 조회에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "USER 또는 ADMIN 권한이 필요합니다.")
    })
    @GetMapping
    public ApiResponse<List<SubscriptionResponseDTO>> mySubscriptions() {
        return ApiResponse.ok(subscriptionService.mySubscriptions());
    }

    @Operation(summary = "중도 해지", description = "가입 상품을 중도 해지하고 현재까지 계산된 해지 이자를 반환합니다.")
    @Parameters({
            @Parameter(name = "subscriptionId", description = "가입 ID", example = "1")
    })
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "중도 해지에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "가입 상태가 유효하지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "본인 가입 내역만 해지할 수 있습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 가입 내역이 없습니다.")
    })
    @PostMapping("/{subscriptionId}/terminate")
    public ApiResponse<SubscriptionResponseDTO> terminate(@PathVariable Long subscriptionId) {
        return ApiResponse.ok(subscriptionService.terminate(subscriptionId));
    }

    @Operation(summary = "상품 계좌 이체", description = "입출금 계좌에서 상품 계좌로 금액을 이체합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이체에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증에 실패했거나 잔액이 부족합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "본인 계좌 또는 가입 내역만 사용할 수 있습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "계좌 또는 가입 내역이 없습니다.")
    })
    @PostMapping("/transfer")
    public ApiResponse<Void> transfer(@Valid @RequestBody TransferRequestDTO request) {
        subscriptionService.transfer(request);
        return ApiResponse.okMessage("이체가 완료되었습니다.");
    }
}
