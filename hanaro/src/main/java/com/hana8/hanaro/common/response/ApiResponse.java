package com.hana8.hanaro.common.response;

import lombok.Builder;

@Builder
public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data
) {
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("OK")
                .message("요청이 성공했습니다.")
                .data(data)
                .build();
    }

    public static ApiResponse<Void> okMessage(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .code("OK")
                .message(message)
                .build();
    }
}
