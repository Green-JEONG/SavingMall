package com.hana8.hanaro.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity.status(code.getStatus())
                .body(new ErrorResponse(false, code.getCode(), code.getMessage(), request.getRequestURI(), LocalDateTime.now(), List.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldErrorDetail> details = e.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .toList();
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(false, ErrorCode.BAD_REQUEST.getCode(), "입력값 검증에 실패했습니다.", request.getRequestURI(), LocalDateTime.now(), details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        List<FieldErrorDetail> details = e.getConstraintViolations().stream()
                .map(v -> new FieldErrorDetail(v.getPropertyPath().toString(), String.valueOf(v.getInvalidValue()), v.getMessage()))
                .toList();
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(false, ErrorCode.BAD_REQUEST.getCode(), "입력값 검증에 실패했습니다.", request.getRequestURI(), LocalDateTime.now(), details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception", e);
        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(code.getStatus())
                .body(new ErrorResponse(false, code.getCode(), code.getMessage(), request.getRequestURI(), LocalDateTime.now(), List.of()));
    }

    private FieldErrorDetail toFieldError(FieldError fieldError) {
        Object rejectedValue = fieldError.getRejectedValue();
        return new FieldErrorDetail(
                fieldError.getField(),
                rejectedValue == null ? "null" : String.valueOf(rejectedValue),
                fieldError.getDefaultMessage() == null ? "invalid" : fieldError.getDefaultMessage()
        );
    }
}
