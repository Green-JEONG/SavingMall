package com.hana8.hanaro.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.common.enums.Role;
import java.time.LocalDateTime;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmailAndNicknameAndPhoneNumber() {
        userRepository.save(User.builder()
                .email("user@test.com")
                .password("pw")
                .nickname("nick")
                .phoneNumber("01011112222")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build());

        assertThat(userRepository.existsByEmail("user@test.com")).isTrue();
        assertThat(userRepository.existsByNickname("nick")).isTrue();
        assertThat(userRepository.existsByPhoneNumber("01011112222")).isTrue();
        assertThat(userRepository.findByEmail("user@test.com")).isPresent();
    }

    @Test
    void findByEmailOrNicknameOrPhoneNumberContaining() {
        userRepository.save(User.builder()
                .email("alpha@test.com")
                .password("pw")
                .nickname("alpha")
                .phoneNumber("01099990000")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build());
        userRepository.save(User.builder()
                .email("beta@test.com")
                .password("pw")
                .nickname("tester-beta")
                .phoneNumber("01088887777")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build());

        assertThat(userRepository.findByEmailContainingIgnoreCaseOrNicknameContainingIgnoreCaseOrPhoneNumberContaining(
                "ALPHA", "ALPHA", "ALPHA"
        )).extracting(User::getEmail).containsExactly("alpha@test.com");

        assertThat(userRepository.findByEmailContainingIgnoreCaseOrNicknameContainingIgnoreCaseOrPhoneNumberContaining(
                "beta", "beta", "beta"
        )).extracting(User::getEmail, User::getNickname)
                .containsExactly(Tuple.tuple("beta@test.com", "tester-beta"));
    }
}
