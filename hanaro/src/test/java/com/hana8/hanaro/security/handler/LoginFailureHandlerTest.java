package com.hana8.hanaro.security.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;

class LoginFailureHandlerTest {

    @Test
    void writesLoginFailureMessage() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        new LoginFailureHandler().onAuthenticationFailure(
                new MockHttpServletRequest(),
                response,
                new AuthenticationServiceException("bad credentials")
        );

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}
