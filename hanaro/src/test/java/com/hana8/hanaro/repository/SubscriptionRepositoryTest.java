package com.hana8.hanaro.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.entity.Account;
import com.hana8.hanaro.repository.AccountRepository;
import com.hana8.hanaro.entity.Product;
import com.hana8.hanaro.repository.ProductRepository;
import com.hana8.hanaro.entity.Subscription;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.repository.UserRepository;
import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.Role;
import com.hana8.hanaro.common.enums.SubscriptionStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void findByUser() {
        User user = userRepository.save(User.builder()
                .email("sub@test.com")
                .password("pw")
                .nickname("sub")
                .phoneNumber("01044445555")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build());

        Account account = accountRepository.save(Account.builder()
                .accountNumber("11112222333")
                .accountNumberFormatted("111-1222-2333")
                .user(user)
                .balance(BigDecimal.ZERO)
                .accountType("FREE")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("예금")
                .type(ProductType.DEPOSIT)
                .paymentAmount(BigDecimal.valueOf(100000))
                .periodMonths(6)
                .maturityRate(BigDecimal.valueOf(3.0))
                .terminationRate(BigDecimal.valueOf(1.0))
                .build());

        subscriptionRepository.save(Subscription.builder()
                .user(user)
                .product(product)
                .account(account)
                .joinedAt(LocalDate.now())
                .maturityAt(LocalDate.now().plusMonths(6))
                .status(SubscriptionStatus.ACTIVE)
                .accumulatedInterest(BigDecimal.ZERO)
                .build());

        assertThat(subscriptionRepository.findByUser(user)).hasSize(1);
    }
}
