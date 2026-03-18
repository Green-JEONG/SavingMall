package com.hana8.hanaro.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hana8.hanaro.security.exception.CustomJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;

class JwtUtilTest {

    private static final String SECRET = "test-secret-key-test-secret-key-123456";

    @Test
    void createAndParseAccessToken() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, 3600000);

        String token = jwtUtil.createAccessToken("user@test.com", "ROLE_USER");

        assertThat(jwtUtil.extractEmail(token)).isEqualTo("user@test.com");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("ROLE_USER");
    }

    @Test
    void throwsCustomExceptionForExpiredToken() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, 3600000);
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
                .subject("user@test.com")
                .claim("role", "ROLE_USER")
                .issuedAt(new java.util.Date(System.currentTimeMillis() - 10_000))
                .expiration(new java.util.Date(System.currentTimeMillis() - 1_000))
                .signWith(key)
                .compact();

        assertThatThrownBy(() -> jwtUtil.parseClaims(expiredToken))
                .isInstanceOf(CustomJwtException.class)
                .hasMessage("로그인이 만료되었습니다. 다시 로그인해 주세요.");
    }

    @Test
    void throwsWhenAuthenticationPrincipalIsInvalid() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, 3600000);

        assertThatThrownBy(() -> jwtUtil.createAccessToken(new TestingAuthenticationToken("plain", null)))
                .isInstanceOf(CustomJwtException.class)
                .hasMessage("로그인 사용자 정보를 확인할 수 없습니다.");
    }
}
