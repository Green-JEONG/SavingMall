package com.hana8.hanaro.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hana8.hanaro.common.enums.Role;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.repository.UserRepository;
import com.hana8.hanaro.service.CustomUserDetailsService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {

    private static final String SECRET = "test-secret-key-test-secret-key-123456";

    @Test
    void setsSecurityContextForValidToken() throws Exception {
        JwtUtil jwtUtil = new JwtUtil(SECRET, 3600000);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user()));
        CustomUserDetailsService userDetailsService = new CustomUserDetailsService(userRepository);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwtUtil.createAccessToken("user@test.com", "ROLE_USER"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) ->
                assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull());

        assertThat(response.getStatus()).isEqualTo(200);
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsUnauthorizedForInvalidToken() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                new JwtUtil(SECRET, 3600000),
                new CustomUserDetailsService(mock(UserRepository.class))
        );
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(jakarta.servlet.FilterChain.class));

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("UNAUTHORIZED");
    }

    @Test
    void skipsExcludedPathWithoutJwtValidation() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                new JwtUtil(SECRET, 3600000),
                new CustomUserDetailsService(mock(UserRepository.class))
        );
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        filter.doFilter(request, response, (req, res) -> chainCalled.set(true));

        assertThat(chainCalled).isTrue();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    private User user() {
        return User.builder()
                .email("user@test.com")
                .password("encoded")
                .nickname("tester")
                .phoneNumber("01012345678")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
