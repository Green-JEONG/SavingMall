package com.hana8.hanaro.repository;

import com.hana8.hanaro.entity.QUser;
import com.hana8.hanaro.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    List<User> findByEmailContainingIgnoreCaseOrNicknameContainingIgnoreCaseOrPhoneNumberContaining(
            String emailKeyword,
            String nicknameKeyword,
            String phoneNumberKeyword
    );

    default List<User> searchByKeyword(String keyword) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (normalizedKeyword.isEmpty()) {
            return findAll(Sort.by(Sort.Direction.ASC, "id"));
        }

        QUser user = QUser.user;
        BooleanExpression predicate = user.email.containsIgnoreCase(normalizedKeyword)
                .or(user.nickname.containsIgnoreCase(normalizedKeyword))
                .or(user.phoneNumber.contains(normalizedKeyword));

        return StreamSupport.stream(findAll(predicate).spliterator(), false)
                .toList();
    }
}
