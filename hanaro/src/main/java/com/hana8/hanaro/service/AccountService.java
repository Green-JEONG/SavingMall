package com.hana8.hanaro.service;

import com.hana8.hanaro.entity.Account;
import com.hana8.hanaro.repository.AccountRepository;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.common.exception.BusinessException;
import com.hana8.hanaro.common.exception.ErrorCode;
import com.hana8.hanaro.common.util.AccountNumberFormatter;
import java.math.BigDecimal;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new BusinessException(ErrorCode.DUPLICATE_ACCOUNT);
        }
        return createAccount(user, accountNumber, accountType);
    }

    private Account createAccount(User user, String accountNumber, String accountType) {
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountNumberFormatted(AccountNumberFormatter.format(accountNumber))
                .user(user)
                .balance(BigDecimal.ZERO)
                .accountType(accountType)
                .build();
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private String generateUniqueAccountNumber() {
        String account;
        do {
            account = String.format("%011d", Math.abs(secureRandom.nextLong()) % 100_000_000_000L);
        } while (accountRepository.existsByAccountNumber(account));
        return account;
    }
}
