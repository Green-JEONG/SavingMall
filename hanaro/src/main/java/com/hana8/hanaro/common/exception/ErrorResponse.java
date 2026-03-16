package com.hana8.hanaro.common.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        boolean success,
        String code,
        String message,
        String path,
        LocalDateTime timestamp,
        List<FieldErrorDetail> errors
) {
}
