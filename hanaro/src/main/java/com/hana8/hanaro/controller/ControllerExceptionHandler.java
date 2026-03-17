package com.hana8.hanaro.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ControllerExceptionHandler {
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
