package com.hana8.hanaro.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountNumberValidator implements ConstraintValidator<AccountNumber, String> {
    private static final String ACCOUNT_PATTERN = "^\\d{11}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(ACCOUNT_PATTERN);
    }
}
