package com.hana8.hanaro.controller;

import com.hana8.hanaro.security.exception.CustomJwtException;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    // 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalExceptionHandler(IllegalArgumentException e) {
        String message = e.getMessage();
        return ResponseEntity.badRequest().body("Warn: " + message);
    }

    // 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleNotValidExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, String> map = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Objects.toString(fieldError.getDefaultMessage(), "Not Valid!"),
                        (existing, newValue) -> existing + ", " + newValue,
                        LinkedHashMap::new
                ));
        return ResponseEntity.badRequest().body(map);
    }

    // 400 Bad Request
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleViolationExceptionHandler(ConstraintViolationException e) {
        Map<String, String> map = e.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> Objects.toString(violation.getMessage(), "Violation Value!"),
                        (existing, newValue) -> existing + ", " + newValue
                ));
        return ResponseEntity.badRequest().body(map);
    }

    // 403 Forbidden
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AuthorizationDeniedException e) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean unauthenticated = authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken;

        if (unauthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "UNAUTHORIZED", "message", "인증이 필요합니다."));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "FORBIDDEN", "message", "접근 권한이 없습니다."));
    }

    // 401 Unauthorized
    @ExceptionHandler(CustomJwtException.class)
    public ResponseEntity<Map<String, String>> handleJwtException(CustomJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
    }

    // Variable: e.g. 400, 401, 404, 409, 500
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException e) {
        String message = e.getReason() == null ? "요청을 처리할 수 없습니다." : e.getReason();
        if (e.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(e.getStatusCode()).body("Warn: " + message);
        }
        return ResponseEntity.status(e.getStatusCode()).body("Error: " + message);
    }

    // 404 Not Found
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NoHandlerFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // 413 Payload Too Large
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("Warn: 업로드 가능한 파일 크기를 초과했습니다.");
    }

    // 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOthersExceptionHandler(Exception e) {
        String message = e.getMessage();
        e.printStackTrace(System.out);
        return ResponseEntity.internalServerError().body("Error: " + message);
    }
}
