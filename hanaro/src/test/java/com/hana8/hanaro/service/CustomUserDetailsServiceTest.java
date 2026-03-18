package com.hana8.hanaro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hana8.hanaro.common.enums.Role;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class CustomUserDetailsServiceTest {

    @Test
    void loadsUserDetailsFromRepository() {
        UserRepository repository = mock(UserRepository.class);
        when(repository.findByEmail("user@test.com")).thenReturn(Optional.of(user()));
        CustomUserDetailsService service = new CustomUserDetailsService(repository);

        assertThat(service.loadUserByUsername("user@test.com").getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void throwsWhenUserIsMissing() {
        UserRepository repository = mock(UserRepository.class);
        when(repository.findByEmail("missing@test.com")).thenReturn(Optional.empty());
        CustomUserDetailsService service = new CustomUserDetailsService(repository);

        assertThatThrownBy(() -> service.loadUserByUsername("missing@test.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    private User user() {
        return User.builder()
                .email("user@test.com")
                .password("encoded")
                .nickname("tester")
                .phoneNumber("01012345678")
                .role(Role.ROLE_ADMIN)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
