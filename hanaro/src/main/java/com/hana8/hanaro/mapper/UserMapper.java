package com.hana8.hanaro.mapper;

import com.hana8.hanaro.dto.UserSummaryResponseDTO;
import com.hana8.hanaro.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserSummaryResponseDTO toUserSummaryResponseDTO(User user) {
        return new UserSummaryResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPhoneNumber(),
                user.getRole().name()
        );
    }
}
