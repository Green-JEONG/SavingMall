package com.hana8.hanaro.controller;

import com.hana8.hanaro.dto.SubscribeRequestDTO;
import com.hana8.hanaro.dto.SubscriptionResponseDTO;
import com.hana8.hanaro.dto.TransferRequestDTO;
import com.hana8.hanaro.service.SubscriptionService;
import com.hana8.hanaro.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class UserSubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "상품 가입")
    @PostMapping
    public ApiResponse<SubscriptionResponseDTO> subscribe(@Valid @RequestBody SubscribeRequestDTO request) {
        return ApiResponse.ok(subscriptionService.subscribe(request));
    }

    @Operation(summary = "내 가입 내역")
    @GetMapping
    public ApiResponse<List<SubscriptionResponseDTO>> mySubscriptions() {
        return ApiResponse.ok(subscriptionService.mySubscriptions());
    }

    @Operation(summary = "중도 해지")
    @PostMapping("/{subscriptionId}/terminate")
    public ApiResponse<SubscriptionResponseDTO> terminate(@PathVariable Long subscriptionId) {
        return ApiResponse.ok(subscriptionService.terminate(subscriptionId));
    }

    @Operation(summary = "상품 계좌 이체")
    @PostMapping("/transfer")
    public ApiResponse<Void> transfer(@Valid @RequestBody TransferRequestDTO request) {
        subscriptionService.transfer(request);
        return ApiResponse.okMessage("이체가 완료되었습니다.");
    }
}
