package com.hana8.hanaro.config;

import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.repository.UserRepository;
import com.hana8.hanaro.common.enums.Role;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitLoader implements CommandLineRunner {

    private final SecurityProperties securityProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = securityProperties.getAdmin().getUsername();
        if (adminEmail == null || adminEmail.isBlank()) {
            return;
        }
        String encodedPassword = passwordEncoder.encode(securityProperties.getAdmin().getPassword());
        userRepository.findByEmail(adminEmail)
                .ifPresentOrElse(
                        existingAdmin -> {
                            existingAdmin.syncAdmin(encodedPassword, securityProperties.getAdmin().getNickname());
                            userRepository.save(existingAdmin);
                        },
                        () -> userRepository.save(User.builder()
                                .email(adminEmail)
                                .password(encodedPassword)
                                .nickname(securityProperties.getAdmin().getNickname())
                                .phoneNumber("01099998888")
                                .role(Role.ROLE_ADMIN)
                                .createdAt(LocalDateTime.now())
                                .build())
                );
    }
}
