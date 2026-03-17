package com.hana8.hanaro.security;

import com.hana8.hanaro.security.exception.CustomJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private static final long DEFAULT_ACCESS_TOKEN_EXPIRATION_MS = Duration.ofHours(1).toMillis();

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;

    public JwtUtil(
            @Value("${jwt.secret:${app.security.jwt.secret}}") String jwtSecret,
            @Value("${jwt.access-token-expiration-ms:${app.security.jwt.access-token-expiration-ms:3600000}}") long accessTokenExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs > 0 ? accessTokenExpirationMs : DEFAULT_ACCESS_TOKEN_EXPIRATION_MS;
    }

    public String createAccessToken(String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public String createAccessToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            throw new CustomJwtException("인증 정보가 올바르지 않습니다.");
        }

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new CustomJwtException("권한 정보가 없습니다."));

        return createAccessToken(userDetails.getUsername(), role);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException("만료된 토큰입니다.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomJwtException("유효하지 않은 토큰입니다.");
        }
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }
}
