package com.hana8.hanaro.controller;

import com.hana8.hanaro.dto.SubscribeRequest;
import com.hana8.hanaro.dto.SubscriptionResponse;
import com.hana8.hanaro.dto.TransferRequest;
import com.hana8.hanaro.service.SubscriptionService;
import com.hana8.hanaro.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/subscriptions")
@RequiredArgsConstructor
public class UserSubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "상품 가입")
    @PostMapping
    public ApiResponse<SubscriptionResponse> subscribe(@Valid @RequestBody SubscribeRequest request) {
        return ApiResponse.ok(subscriptionService.subscribe(request));
    }

    @Operation(summary = "내 가입 내역")
    @GetMapping
    public ApiResponse<List<SubscriptionResponse>> mySubscriptions() {
        return ApiResponse.ok(subscriptionService.mySubscriptions());
    }

    @Operation(summary = "중도 해지")
    @PostMapping("/{subscriptionId}/terminate")
    public ApiResponse<SubscriptionResponse> terminate(@PathVariable Long subscriptionId) {
        return ApiResponse.ok(subscriptionService.terminate(subscriptionId));
    }

    @Operation(summary = "상품 계좌 이체")
    @PostMapping("/transfer")
    public ApiResponse<Void> transfer(@Valid @RequestBody TransferRequest request) {
        subscriptionService.transfer(request);
        return ApiResponse.okMessage("이체가 완료되었습니다.");
    }
}
