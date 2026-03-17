package com.hana8.hanaro.security.exception;

public class CustomJwtException extends RuntimeException {

    public CustomJwtException(String message) {
        super(message);
    }
}
