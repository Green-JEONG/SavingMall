package com.hana8.hanaro.controller;

import com.hana8.hanaro.dto.SubscriptionResponseDTO;
import com.hana8.hanaro.service.SubscriptionService;
import com.hana8.hanaro.dto.UserSummaryResponseDTO;
import com.hana8.hanaro.service.UserService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "관리자 회원 API", description = "관리자의 회원 목록 조회, 회원별 가입 내역 조회, 만기 처리 API입니다.")
public class AdminUserController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @Operation(summary = "회원 목록 조회 및 검색", description = "회원 전체 목록을 조회하거나 이메일, 닉네임, 전화번호로 검색합니다.")
    @Parameters({
            @Parameter(name = "keyword", description = "검색 키워드", example = "hana")
    })
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 목록 조회에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.")
    })
    @GetMapping
    public ApiResponse<List<UserSummaryResponseDTO>> users(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(userService.getAllUsers(keyword));
    }

    @Operation(summary = "회원별 가입 내역 조회", description = "특정 회원의 가입 상품 내역을 조회합니다.")
    @Parameters({
            @Parameter(name = "userId", description = "회원 ID", example = "1")
    })
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "가입 내역 조회에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 회원 또는 가입 내역이 없습니다.")
    })
    @GetMapping("/{userId}/subscriptions")
    public ApiResponse<List<SubscriptionResponseDTO>> userSubscriptions(@PathVariable Long userId) {
        return ApiResponse.ok(subscriptionService.subscriptionsByUser(userId));
    }

    @Operation(summary = "만기 처리", description = "오늘 기준 만기 도래한 가입 상품을 일괄 처리합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "만기 처리에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.")
    })
    @PostMapping("/maturity")
    public ApiResponse<Integer> processMaturity() {
        return ApiResponse.ok(subscriptionService.processMaturedSubscriptions());
    }
}
