package com.hana8.hanaro.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.common.enums.Role;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AccountTest {

    @Test
    void addAndSubtractBalanceUpdateState() {
        Account account = Account.builder()
                .accountNumber("12345678901")
                .accountNumberFormatted("123-4567-8901")
                .user(user())
                .balance(BigDecimal.valueOf(1000))
                .accountType("FREE")
                .build();

        account.addBalance(BigDecimal.valueOf(500));
        account.subtractBalance(BigDecimal.valueOf(200));

        assertThat(account.getBalance()).isEqualByComparingTo("1300");
    }

    private User user() {
        return User.builder()
                .email("user@test.com")
                .password("password")
                .nickname("tester")
                .phoneNumber("01012345678")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
