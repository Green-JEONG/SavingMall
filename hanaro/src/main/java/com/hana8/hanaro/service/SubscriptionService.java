package com.hana8.hanaro.service;

import com.hana8.hanaro.entity.Account;
import com.hana8.hanaro.service.AccountService;
import com.hana8.hanaro.entity.Product;
import com.hana8.hanaro.service.ProductService;
import com.hana8.hanaro.dto.SubscribeRequest;
import com.hana8.hanaro.dto.SubscriptionResponse;
import com.hana8.hanaro.dto.TransferRequest;
import com.hana8.hanaro.entity.Subscription;
import com.hana8.hanaro.repository.SubscriptionRepository;
import com.hana8.hanaro.entity.TransactionHistory;
import com.hana8.hanaro.repository.TransactionHistoryRepository;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.service.UserService;
import com.hana8.hanaro.common.enums.SubscriptionStatus;
import com.hana8.hanaro.common.enums.TransactionType;
import com.hana8.hanaro.common.exception.BusinessException;
import com.hana8.hanaro.common.exception.ErrorCode;
import com.hana8.hanaro.common.logging.LogEventPublisher;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final ProductService productService;
    private final AccountService accountService;
    private final UserService userService;
    private final LogEventPublisher logEventPublisher;

    @Transactional
    public SubscriptionResponse subscribe(SubscribeRequest request) {
        User user = userService.currentUser();
        Product product = productService.findProduct(request.productId());
        Account account = accountService.createProductAccount(user, request.accountNumber(), product.getType().name());

        Subscription subscription = Subscription.builder()
                .user(user)
                .product(product)
                .account(account)
                .joinedAt(LocalDate.now())
                .maturityAt(LocalDate.now().plusMonths(product.getPeriodMonths()))
                .status(SubscriptionStatus.ACTIVE)
                .accumulatedInterest(BigDecimal.ZERO)
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        saveHistory(saved, TransactionType.SUBSCRIBE, product.getPaymentAmount(), "상품 가입");
        logEventPublisher.service("상품 가입: user=" + user.getEmail() + ", product=" + product.getName()
                + ", account=" + account.getAccountNumberFormatted());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> mySubscriptions() {
        User user = userService.currentUser();
        return subscriptionRepository.findByUser(user).stream().map(this::toResponse).toList();
    }

    @Transactional
    public SubscriptionResponse terminate(Long subscriptionId) {
        User user = userService.currentUser();
        Subscription subscription = findById(subscriptionId);
        if (!subscription.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.INVALID_SUBSCRIPTION_STATUS);
        }

        BigDecimal interest = calculateInterest(subscription, subscription.getProduct().getTerminationRate());
        subscription.terminate(interest);
        subscription.getAccount().addBalance(subscription.getProduct().getPaymentAmount().add(interest));
        saveHistory(subscription, TransactionType.TERMINATE, interest, "중도 해지");
        logEventPublisher.service("상품 해지: subscriptionId=" + subscription.getId() + ", interest=" + interest);
        return toResponse(subscription);
    }

    @Transactional
    public void transfer(TransferRequest request) {
        User user = userService.currentUser();
        Subscription subscription = findById(request.subscriptionId());
        if (!subscription.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Account fromAccount = accountService.findByAccountNumber(request.fromAccountNumber());
        if (!fromAccount.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if (fromAccount.getBalance().compareTo(request.amount()) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        fromAccount.subtractBalance(request.amount());
        subscription.getAccount().addBalance(request.amount());
        saveHistory(subscription, TransactionType.TRANSFER, request.amount(), "상품 계좌 이체");
        logEventPublisher.service("이체: subscriptionId=" + subscription.getId() + ", amount=" + request.amount());
    }

    @Transactional
    public int processMaturedSubscriptions() {
        LocalDate today = LocalDate.now();
        List<Subscription> all = subscriptionRepository.findAll();
        int processed = 0;
        for (Subscription subscription : all) {
            if (subscription.getStatus() == SubscriptionStatus.ACTIVE && !subscription.getMaturityAt().isAfter(today)) {
                BigDecimal interest = calculateInterest(subscription, subscription.getProduct().getMaturityRate());
                subscription.mature(interest);
                subscription.getAccount().addBalance(subscription.getProduct().getPaymentAmount().add(interest));
                saveHistory(subscription, TransactionType.TERMINATE, interest, "만기 처리");
                processed++;
            }
        }
        return processed;
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> subscriptionsByUser(Long userId) {
        List<Subscription> all = subscriptionRepository.findAll();
        return all.stream()
                .filter(subscription -> subscription.getUser().getId().equals(userId))
                .map(this::toResponse)
                .toList();
    }

    private BigDecimal calculateInterest(Subscription subscription, BigDecimal rate) {
        long days = Math.max(1, ChronoUnit.DAYS.between(subscription.getJoinedAt(), LocalDate.now()));
        BigDecimal principal = subscription.getProduct().getPaymentAmount();
        BigDecimal dayRate = rate.divide(BigDecimal.valueOf(36500), 10, RoundingMode.HALF_UP);
        return principal.multiply(dayRate).multiply(BigDecimal.valueOf(days)).setScale(2, RoundingMode.HALF_UP);
    }

    private void saveHistory(Subscription subscription, TransactionType type, BigDecimal amount, String description) {
        transactionHistoryRepository.save(TransactionHistory.builder()
                .subscription(subscription)
                .type(type)
                .amount(amount)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private Subscription findById(Long subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
    }

    private SubscriptionResponse toResponse(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getProduct().getName(),
                subscription.getAccount().getAccountNumberFormatted(),
                subscription.getStatus(),
                subscription.getJoinedAt(),
                subscription.getMaturityAt(),
                subscription.getAccumulatedInterest()
        );
    }
}
