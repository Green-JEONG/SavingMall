package com.hana8.hanaro.security;

import com.hana8.hanaro.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class JwtProvider {

    private final SecurityProperties securityProperties;

    public JwtProvider(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public String createToken(String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + securityProperties.getJwt().getAccessTokenExpirationMs());

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey())
                .compact();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }
    }

    public String resolveEmail(String token) {
        return parseClaims(token).getSubject();
    }

    private Key signingKey() {
        return Keys.hmacShaKeyFor(securityProperties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
