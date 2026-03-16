package com.hana8.hanaro.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Component
public class ApiRequestLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_LENGTH = 1000;
    private static final Pattern SECRET_FIELD = Pattern.compile(
            "(\"(?:password|accessToken|refreshToken|token|secret)\"\\s*:\\s*\")([^\"]*)(\")",
            Pattern.CASE_INSENSITIVE
    );

    private final LogEventPublisher logEventPublisher;

    public ApiRequestLoggingFilter(LogEventPublisher logEventPublisher) {
        this.logEventPublisher = logEventPublisher;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, MAX_PAYLOAD_LENGTH);
        filterChain.doFilter(wrappedRequest, response);
        publish(wrappedRequest, response);
    }

    private void publish(ContentCachingRequestWrapper request, HttpServletResponse response) {
        String message = "API 요청: method=" + request.getMethod()
                + ", uri=" + request.getRequestURI()
                + ", status=" + response.getStatus()
                + ", payload=" + payload(request);

        String uri = request.getRequestURI();
        if (uri.startsWith("/api/auth") || uri.startsWith("/api/admin/users")) {
            logEventPublisher.user(message);
            return;
        }
        if (uri.startsWith("/api/admin/products") || uri.startsWith("/api/user/products")) {
            logEventPublisher.product(message);
            return;
        }
        if (uri.contains("/terminate") || ("POST".equals(request.getMethod()) && uri.startsWith("/api/user/subscriptions"))) {
            logEventPublisher.subscription(message);
            return;
        }
        if (uri.contains("/transfer") || uri.startsWith("/api/user/subscriptions")) {
            logEventPublisher.service(message);
            return;
        }
        logEventPublisher.application(message);
    }

    private String payload(ContentCachingRequestWrapper request) {
        String contentType = request.getContentType();
        if (contentType != null && contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            return "multipart parts=" + request.getParameterMap().keySet();
        }
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return request.getQueryString() == null ? "{}" : "query=" + request.getQueryString();
        }
        String payload = new String(content, StandardCharsets.UTF_8);
        payload = SECRET_FIELD.matcher(payload).replaceAll("$1***$3");
        if (payload.length() > MAX_PAYLOAD_LENGTH) {
            return payload.substring(0, MAX_PAYLOAD_LENGTH) + "...";
        }
        return payload;
    }
}
