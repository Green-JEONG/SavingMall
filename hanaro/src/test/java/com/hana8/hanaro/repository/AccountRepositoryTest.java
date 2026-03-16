package com.hana8.hanaro.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.entity.Account;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.repository.UserRepository;
import com.hana8.hanaro.common.enums.Role;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByAccountNumberAndUser() {
        User user = userRepository.save(User.builder()
                .email("acc@test.com")
                .password("pw")
                .nickname("acc")
                .phoneNumber("01033334444")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build());

        accountRepository.save(Account.builder()
                .accountNumber("12345678901")
                .accountNumberFormatted("123-4567-8901")
                .user(user)
                .balance(BigDecimal.ZERO)
                .accountType("FREE")
                .build());

        assertThat(accountRepository.existsByAccountNumber("12345678901")).isTrue();
        assertThat(accountRepository.findByAccountNumber("12345678901")).isPresent();
        assertThat(accountRepository.findByUser(user)).hasSize(1);
    }
}
