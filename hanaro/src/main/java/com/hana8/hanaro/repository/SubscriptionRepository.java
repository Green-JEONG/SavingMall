package com.hana8.hanaro.repository;

import com.hana8.hanaro.entity.Subscription;
import com.hana8.hanaro.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUser(User user);
}
