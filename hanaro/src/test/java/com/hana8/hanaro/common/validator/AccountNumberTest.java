package com.hana8.hanaro.common.validator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AccountNumberTest {

    private record Sample(@AccountNumber String accountNumber) {
    }

    @Test
    void defaultMessageMatchesExpectedText() throws Exception {
        AccountNumber annotation = Sample.class.getDeclaredField("accountNumber").getAnnotation(AccountNumber.class);

        assertThat(annotation.message()).isEqualTo("계좌번호는 하이픈 없이 숫자 11자리여야 합니다.");
    }
}
