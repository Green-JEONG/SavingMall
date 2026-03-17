package com.hana8.hanaro.mapper;

import com.hana8.hanaro.common.enums.Role;
import com.hana8.hanaro.dto.AuthResponseDTO;
import com.hana8.hanaro.dto.SignUpRequestDTO;
import com.hana8.hanaro.entity.User;
import java.time.LocalDateTime;

public final class AuthMapper {

    private AuthMapper() {
    }

    public static User toUser(SignUpRequestDTO request, String encodedPassword) {
        return User.builder()
                .email(request.email())
                .password(encodedPassword)
                .nickname(request.nickname())
                .phoneNumber(request.phoneNumber())
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static AuthResponseDTO toAuthResponseDTO(String accessToken) {
        return new AuthResponseDTO(accessToken, "Bearer");
    }
}
