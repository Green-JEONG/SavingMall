package com.hana8.hanaro.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.common.enums.Role;
import java.time.LocalDateTime;
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
}
