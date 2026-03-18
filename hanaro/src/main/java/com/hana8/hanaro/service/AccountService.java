package com.hana8.hanaro.service;

import com.hana8.hanaro.entity.Account;
import com.hana8.hanaro.repository.AccountRepository;
import com.hana8.hanaro.entity.User;
import java.math.BigDecimal;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public Account createFreeAccount(User user) {
        String accountNumber = generateUniqueAccountNumber();
        return createAccount(user, accountNumber, "FREE");
    }

    @Transactional
    public Account createProductAccount(User user, String accountNumber, String accountType) {
        if (accountNumber == null || !accountNumber.matches("\\d{11}")) {
            throw new IllegalArgumentException("계좌번호는 숫자 11자리여야 합니다.");
        }
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 계좌번호입니다.");
        }
        return createAccount(user, accountNumber, accountType);
    }

    private Account createAccount(User user, String accountNumber, String accountType) {
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountNumberFormatted(formatAccountNumber(accountNumber))
                .user(user)
                .balance(BigDecimal.ZERO)
                .accountType(accountType)
                .build();
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "계좌를 찾을 수 없습니다."));
    }

    private String generateUniqueAccountNumber() {
        String account;
        do {
            account = String.format("%011d", Math.abs(secureRandom.nextLong()) % 100_000_000_000L);
        } while (accountRepository.existsByAccountNumber(account));
        return account;
    }

    private String formatAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() != 11) {
            return accountNumber;
        }
        return accountNumber.substring(0, 3) + "-" + accountNumber.substring(3, 7) + "-" + accountNumber.substring(7);
    }
}
