package com.hana8.hanaro.security.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;

class CustomAuthenticationEntryPointTest {

    @Test
    void writesUnauthorizedJsonResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        new CustomAuthenticationEntryPoint().commence(
                new MockHttpServletRequest(),
                response,
                new AuthenticationServiceException("fail")
        );

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("인증이 필요합니다.");
    }
}
