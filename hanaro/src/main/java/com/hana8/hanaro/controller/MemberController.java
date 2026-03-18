package com.hana8.hanaro.controller;

import com.hana8.hanaro.common.response.ApiResponse;
import com.hana8.hanaro.dto.SubscriptionResponseDTO;
import com.hana8.hanaro.dto.UserSummaryResponseDTO;
import com.hana8.hanaro.service.SubscriptionService;
import com.hana8.hanaro.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @Operation(summary = "내 회원 정보 조회")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ApiResponse<UserSummaryResponseDTO> me() {
        return ApiResponse.ok(userService.currentUserSummary());
    }

    @Operation(summary = "회원 목록 조회 및 검색")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<List<UserSummaryResponseDTO>> members(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(userService.getAllUsers(keyword));
    }

    @Operation(summary = "회원별 가입 내역 조회")
    @GetMapping("/{userId}/subscriptions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<List<SubscriptionResponseDTO>> memberSubscriptions(@PathVariable Long userId) {
        return ApiResponse.ok(subscriptionService.subscriptionsByUser(userId));
    }

    @Operation(summary = "만기 처리")
    @PostMapping("/maturity")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Integer> processMaturity() {
        return ApiResponse.ok(subscriptionService.processMaturedSubscriptions());
    }
}
