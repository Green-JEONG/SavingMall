package com.hana8.hanaro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.hana8.hanaro.common.enums.Role;
import com.hana8.hanaro.entity.Account;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.repository.AccountRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void givenUser_whenCreateFreeAccount_thenFormattedFreeAccountIsSaved() {
        User user = User.builder()
                .email("user@test.com")
                .password("password")
                .nickname("tester")
                .phoneNumber("01012345678")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();
        given(accountRepository.existsByAccountNumber(any(String.class))).willReturn(false);
        given(accountRepository.save(any(Account.class))).willAnswer(invocation -> invocation.getArgument(0));

        Account account = accountService.createFreeAccount(user);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        Account savedAccount = captor.getValue();

        assertThat(savedAccount.getUser()).isEqualTo(user);
        assertThat(savedAccount.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(savedAccount.getAccountType()).isEqualTo("FREE");
        assertThat(savedAccount.getAccountNumber()).hasSize(11).containsOnlyDigits();
        assertThat(savedAccount.getAccountNumberFormatted()).matches("\\d{3}-\\d{4}-\\d{4}");
        assertThat(account).isEqualTo(savedAccount);
    }

    @Test
    void givenMissingAccountNumber_whenFindByAccountNumber_thenAccountNotFoundExceptionIsThrown() {
        given(accountRepository.findByAccountNumber("12345678901")).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findByAccountNumber("12345678901"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void givenInvalidProductAccountNumber_whenCreateProductAccount_thenIllegalArgumentExceptionIsThrown() {
        User user = User.builder()
                .email("user@test.com")
                .password("password")
                .nickname("tester")
                .phoneNumber("01012345678")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();

        assertThatThrownBy(() -> accountService.createProductAccount(user, "123-4567-8901", "SAVINGS"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("계좌번호는 하이픈 없이 숫자 11자리여야 합니다.");
    }
}
