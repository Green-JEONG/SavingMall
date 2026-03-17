package com.hana8.hanaro.controller;

import java.util.Map;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        return ResponseEntity.status(403)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("error", "FORBIDDEN", "message", "접근 권한이 없습니다."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalExceptionHandler(IllegalArgumentException e) {
        String message = e.getMessage();
        return ResponseEntity.badRequest().body("Warn: " + message);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusExceptionHandler(ResponseStatusException e) {
        HttpStatusCode statusCode = e.getStatusCode();
        String message = e.getReason() == null ? "요청을 처리할 수 없습니다." : e.getReason();
        if (statusCode.is4xxClientError()) {
            return ResponseEntity.status(statusCode).body("Warn: " + message);
        }
        return ResponseEntity.status(statusCode).body("Error: " + message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOthersExceptionHandler(Exception e) {
        String message = e.getMessage();
        return ResponseEntity.internalServerError().body("Error: " + message);
    }
}
