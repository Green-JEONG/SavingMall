package com.hana8.hanaro.common.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApiResponseTest {

    @Test
    void okCreatesSuccessResponseWithData() {
        ApiResponse<String> response = ApiResponse.ok("data");

        assertThat(response.success()).isTrue();
        assertThat(response.code()).isEqualTo("OK");
        assertThat(response.message()).isEqualTo("요청이 성공했습니다.");
        assertThat(response.data()).isEqualTo("data");
    }

    @Test
    void okMessageCreatesSuccessResponseWithoutData() {
        ApiResponse<Void> response = ApiResponse.okMessage("완료");

        assertThat(response.success()).isTrue();
        assertThat(response.code()).isEqualTo("OK");
        assertThat(response.message()).isEqualTo("완료");
        assertThat(response.data()).isNull();
    }
}
