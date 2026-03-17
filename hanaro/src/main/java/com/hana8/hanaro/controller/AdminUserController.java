package com.hana8.hanaro.controller;

import com.hana8.hanaro.dto.SubscriptionResponseDTO;
import com.hana8.hanaro.service.SubscriptionService;
import com.hana8.hanaro.dto.UserSummaryResponseDTO;
import com.hana8.hanaro.service.UserService;
import com.hana8.hanaro.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @Operation(summary = "회원 목록 조회 및 검색")
    @GetMapping
    public ApiResponse<List<UserSummaryResponseDTO>> users(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(userService.getAllUsers(keyword));
    }

    @Operation(summary = "회원별 가입 내역 조회")
    @GetMapping("/{userId}/subscriptions")
    public ApiResponse<List<SubscriptionResponseDTO>> userSubscriptions(@PathVariable Long userId) {
        return ApiResponse.ok(subscriptionService.subscriptionsByUser(userId));
    }

    @Operation(summary = "만기 처리")
    @PostMapping("/maturity")
    public ApiResponse<Integer> processMaturity() {
        return ApiResponse.ok(subscriptionService.processMaturedSubscriptions());
    }
}
