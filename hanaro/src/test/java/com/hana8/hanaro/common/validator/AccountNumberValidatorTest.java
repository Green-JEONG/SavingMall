package com.hana8.hanaro.common.validator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AccountNumberValidatorTest {

    private final AccountNumberValidator validator = new AccountNumberValidator();

    @Test
    void acceptsOnlyElevenDigitsWithoutHyphen() {
        assertThat(validator.isValid("12345678901", null)).isTrue();
        assertThat(validator.isValid("123-4567-8901", null)).isFalse();
        assertThat(validator.isValid("1234", null)).isFalse();
    }

    @Test
    void allowsNullOrBlankSoNotBlankCanHandleRequiredValidation() {
        assertThat(validator.isValid(null, null)).isTrue();
        assertThat(validator.isValid("", null)).isTrue();
    }
}
