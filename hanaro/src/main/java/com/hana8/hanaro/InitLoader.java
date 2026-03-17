package com.hana8.hanaro;

import java.time.LocalDateTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hana8.hanaro.common.enums.Role;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.repository.UserRepository;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitLoader implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "hanaro";
    private static final String ADMIN_PASSWORD = "12345678";
    private static final String ADMIN_NICKNAME = "관리자";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(@Nullable ApplicationArguments args) {
        String encodedPassword = passwordEncoder.encode(ADMIN_PASSWORD);
        userRepository.findByEmail(ADMIN_EMAIL)
                .ifPresentOrElse(
                        existingAdmin -> {
                            existingAdmin.syncAdmin(encodedPassword, ADMIN_NICKNAME);
                            userRepository.save(existingAdmin);
                        },
                        () -> userRepository.save(User.builder()
                                .email(ADMIN_EMAIL)
                                .password(encodedPassword)
                                .nickname(ADMIN_NICKNAME)
                                .phoneNumber("01099998888")
                                .role(Role.ROLE_ADMIN)
                                .createdAt(LocalDateTime.now())
                                .build())
                );
    }
}
