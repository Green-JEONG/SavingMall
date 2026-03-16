package com.hana8.hanaro.common.exception;

public record FieldErrorDetail(
        String field,
        String value,
        String reason
) {
}
