package com.hana8.hanaro.security.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hana8.hanaro.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;

class LoginSuccessHandlerTest {

    @Test
    void writesBearerTokenPayload() throws Exception {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        when(jwtUtil.createAccessToken(any())).thenReturn("issued-token");
        LoginSuccessHandler handler = new LoginSuccessHandler(jwtUtil);
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(
                new MockHttpServletRequest(),
                response,
                new TestingAuthenticationToken("user@test.com", null)
        );

        assertThat(response.getContentAsString()).contains("issued-token");
        assertThat(response.getContentAsString()).contains("Bearer");
    }
}
