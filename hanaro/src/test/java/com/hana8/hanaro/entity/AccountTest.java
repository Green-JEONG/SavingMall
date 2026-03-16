package com.hana8.hanaro.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AccountTest {

    @Test
    void givenAccount_whenAddBalance_thenBalanceIncreases() {
        Account account = Account.builder()
                .accountNumber("12345678901")
                .accountNumberFormatted("123-4567-8901")
                .balance(BigDecimal.valueOf(1000))
                .accountType("FREE")
                .build();

        account.addBalance(BigDecimal.valueOf(250));

        assertThat(account.getBalance()).isEqualByComparingTo("1250");
    }

    @Test
    void givenAccount_whenSubtractBalance_thenBalanceDecreases() {
        Account account = Account.builder()
                .accountNumber("12345678901")
                .accountNumberFormatted("123-4567-8901")
                .balance(BigDecimal.valueOf(1000))
                .accountType("FREE")
                .build();

        account.subtractBalance(BigDecimal.valueOf(400));

        assertThat(account.getBalance()).isEqualByComparingTo("600");
    }
}
