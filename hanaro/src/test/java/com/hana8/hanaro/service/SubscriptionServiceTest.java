package com.hana8.hanaro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.Role;
import com.hana8.hanaro.common.enums.SubscriptionStatus;
import com.hana8.hanaro.common.enums.TransactionType;
import com.hana8.hanaro.dto.SubscribeRequestDTO;
import com.hana8.hanaro.dto.SubscriptionResponseDTO;
import com.hana8.hanaro.dto.TransferRequestDTO;
import com.hana8.hanaro.entity.Account;
import com.hana8.hanaro.entity.Product;
import com.hana8.hanaro.entity.Subscription;
import com.hana8.hanaro.entity.TransactionHistory;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.repository.SubscriptionRepository;
import com.hana8.hanaro.repository.TransactionHistoryRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;
    @Mock
    private ProductService productService;
    @Mock
    private AccountService accountService;
    @Mock
    private UserService userService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void givenValidUserProductAndAccount_whenSubscribe_thenSubscriptionAndHistoryAreSaved() {
        User user = user(1L, "user@test.com");
        Product product = product(1L, "적금", BigDecimal.valueOf(300000), 12, BigDecimal.valueOf(4.5), BigDecimal.valueOf(1.5));
        Account account = account(1L, user, BigDecimal.ZERO);
        given(userService.currentUser()).willReturn(user);
        given(productService.findProduct(1L)).willReturn(product);
        given(accountService.createProductAccount(user, "12345678901", "SAVINGS")).willReturn(account);
        given(subscriptionRepository.save(any(Subscription.class))).willAnswer(invocation -> {
            Subscription source = invocation.getArgument(0);
            return Subscription.builder()
                    .id(10L)
                    .user(source.getUser())
                    .product(source.getProduct())
                    .account(source.getAccount())
                    .joinedAt(source.getJoinedAt())
                    .maturityAt(source.getMaturityAt())
                    .status(source.getStatus())
                    .accumulatedInterest(source.getAccumulatedInterest())
                    .build();
        });

        SubscriptionResponseDTO response = subscriptionService.subscribe(new SubscribeRequestDTO(1L, "12345678901"));

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.productName()).isEqualTo("적금");
        assertThat(response.status()).isEqualTo(SubscriptionStatus.ACTIVE);

        ArgumentCaptor<TransactionHistory> historyCaptor = ArgumentCaptor.forClass(TransactionHistory.class);
        verify(transactionHistoryRepository).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue().getType()).isEqualTo(TransactionType.SUBSCRIBE);
        assertThat(historyCaptor.getValue().getAmount()).isEqualByComparingTo("300000");
    }

    @Test
    void givenOwnedActiveSubscription_whenTerminate_thenStatusBalanceAndHistoryAreUpdated() {
        User user = user(1L, "user@test.com");
        Product product = product(1L, "예금", BigDecimal.valueOf(1000000), 12, BigDecimal.valueOf(3.5), BigDecimal.valueOf(1.2));
        Account account = account(1L, user, BigDecimal.valueOf(50000));
        Subscription subscription = Subscription.builder()
                .id(11L)
                .user(user)
                .product(product)
                .account(account)
                .joinedAt(LocalDate.now().minusDays(10))
                .maturityAt(LocalDate.now().plusMonths(12))
                .status(SubscriptionStatus.ACTIVE)
                .accumulatedInterest(BigDecimal.ZERO)
                .build();
        given(userService.currentUser()).willReturn(user);
        given(subscriptionRepository.findById(11L)).willReturn(Optional.of(subscription));

        SubscriptionResponseDTO response = subscriptionService.terminate(11L);
        BigDecimal expectedInterest = calculateInterest(subscription.getJoinedAt(), product.getPaymentAmount(), product.getTerminationRate());

        assertThat(response.status()).isEqualTo(SubscriptionStatus.TERMINATED);
        assertThat(response.accumulatedInterest()).isEqualByComparingTo(expectedInterest);
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(50000).add(product.getPaymentAmount()).add(expectedInterest));

        ArgumentCaptor<TransactionHistory> historyCaptor = ArgumentCaptor.forClass(TransactionHistory.class);
        verify(transactionHistoryRepository).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue().getType()).isEqualTo(TransactionType.TERMINATE);
        assertThat(historyCaptor.getValue().getAmount()).isEqualByComparingTo(expectedInterest);
    }

    @Test
    void givenInsufficientBalance_whenTransfer_thenInsufficientBalanceExceptionIsThrown() {
        User user = user(1L, "user@test.com");
        Account subscriptionAccount = account(1L, user, BigDecimal.ZERO);
        Subscription subscription = Subscription.builder()
                .id(12L)
                .user(user)
                .product(product(1L, "예금", BigDecimal.valueOf(1000000), 12, BigDecimal.valueOf(3.5), BigDecimal.valueOf(1.2)))
                .account(subscriptionAccount)
                .joinedAt(LocalDate.now())
                .maturityAt(LocalDate.now().plusMonths(12))
                .status(SubscriptionStatus.ACTIVE)
                .accumulatedInterest(BigDecimal.ZERO)
                .build();
        Account fromAccount = account(2L, user, BigDecimal.valueOf(1000));
        given(userService.currentUser()).willReturn(user);
        given(subscriptionRepository.findById(12L)).willReturn(Optional.of(subscription));
        given(accountService.findByAccountNumber("12345678901")).willReturn(fromAccount);

        assertThatThrownBy(() -> subscriptionService.transfer(new TransferRequestDTO(12L, BigDecimal.valueOf(5000), "12345678901")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔액이 부족합니다.");
    }

    @Test
    void givenOtherUsersAccount_whenTransfer_thenForbiddenExceptionIsThrown() {
        User user = user(1L, "user@test.com");
        User otherUser = user(2L, "other@test.com");
        Account subscriptionAccount = account(1L, user, BigDecimal.ZERO);
        Subscription subscription = Subscription.builder()
                .id(12L)
                .user(user)
                .product(product(1L, "예금", BigDecimal.valueOf(1000000), 12, BigDecimal.valueOf(3.5), BigDecimal.valueOf(1.2)))
                .account(subscriptionAccount)
                .joinedAt(LocalDate.now())
                .maturityAt(LocalDate.now().plusMonths(12))
                .status(SubscriptionStatus.ACTIVE)
                .accumulatedInterest(BigDecimal.ZERO)
                .build();
        Account otherUsersAccount = account(2L, otherUser, BigDecimal.valueOf(50000));
        given(userService.currentUser()).willReturn(user);
        given(subscriptionRepository.findById(12L)).willReturn(Optional.of(subscription));
        given(accountService.findByAccountNumber("12345678901")).willReturn(otherUsersAccount);

        assertThatThrownBy(() -> subscriptionService.transfer(new TransferRequestDTO(12L, BigDecimal.valueOf(5000), "12345678901")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void givenMaturedAndUnmaturedSubscriptions_whenProcessMaturedSubscriptions_thenOnlyEligibleSubscriptionsAreProcessed() {
        User user = user(1L, "user@test.com");
        Product product = product(1L, "적금", BigDecimal.valueOf(200000), 12, BigDecimal.valueOf(5.0), BigDecimal.valueOf(1.5));
        Account maturedAccount = account(1L, user, BigDecimal.ZERO);
        Account futureAccount = account(2L, user, BigDecimal.ZERO);
        Subscription maturedTarget = Subscription.builder()
                .id(21L)
                .user(user)
                .product(product)
                .account(maturedAccount)
                .joinedAt(LocalDate.now().minusMonths(12))
                .maturityAt(LocalDate.now().minusDays(1))
                .status(SubscriptionStatus.ACTIVE)
                .accumulatedInterest(BigDecimal.ZERO)
                .build();
        Subscription futureTarget = Subscription.builder()
                .id(22L)
                .user(user)
                .product(product)
                .account(futureAccount)
                .joinedAt(LocalDate.now())
                .maturityAt(LocalDate.now().plusMonths(6))
                .status(SubscriptionStatus.ACTIVE)
                .accumulatedInterest(BigDecimal.ZERO)
                .build();
        given(subscriptionRepository.findAll()).willReturn(List.of(maturedTarget, futureTarget));

        int processed = subscriptionService.processMaturedSubscriptions();
        BigDecimal expectedInterest = calculateInterest(maturedTarget.getJoinedAt(), product.getPaymentAmount(), product.getMaturityRate());

        assertThat(processed).isEqualTo(1);
        assertThat(maturedTarget.getStatus()).isEqualTo(SubscriptionStatus.MATURED);
        assertThat(maturedTarget.getAccumulatedInterest()).isEqualByComparingTo(expectedInterest);
        assertThat(maturedAccount.getBalance()).isEqualByComparingTo(product.getPaymentAmount().add(expectedInterest));
        assertThat(futureTarget.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    private User user(Long id, String email) {
        return User.builder()
                .id(id)
                .email(email)
                .password("password")
                .nickname("tester" + id)
                .phoneNumber("0101234" + String.format("%04d", id))
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Product product(Long id, String name, BigDecimal paymentAmount, int periodMonths, BigDecimal maturityRate,
                            BigDecimal terminationRate) {
        return Product.builder()
                .id(id)
                .name(name)
                .type(ProductType.SAVINGS)
                .paymentAmount(paymentAmount)
                .periodMonths(periodMonths)
                .maturityRate(maturityRate)
                .terminationRate(terminationRate)
                .build();
    }

    private Account account(Long id, User user, BigDecimal balance) {
        return Account.builder()
                .id(id)
                .accountNumber("1234567890" + id)
                .accountNumberFormatted("123-4567-890" + id)
                .user(user)
                .balance(balance)
                .accountType("FREE")
                .build();
    }

    private BigDecimal calculateInterest(LocalDate joinedAt, BigDecimal principal, BigDecimal rate) {
        long days = Math.max(1, ChronoUnit.DAYS.between(joinedAt, LocalDate.now()));
        BigDecimal dayRate = rate.divide(BigDecimal.valueOf(36500), 10, RoundingMode.HALF_UP);
        return principal.multiply(dayRate).multiply(BigDecimal.valueOf(days)).setScale(2, RoundingMode.HALF_UP);
    }
}
