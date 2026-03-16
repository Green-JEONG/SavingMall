package com.hana8.hanaro.service;

import com.hana8.hanaro.dto.UserSummaryResponse;
import com.hana8.hanaro.repository.UserRepository;
import com.hana8.hanaro.common.exception.BusinessException;
import com.hana8.hanaro.common.exception.ErrorCode;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.security.SecurityUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User currentUser() {
        return userRepository.findByEmail(SecurityUtil.currentUserEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getAllUsers(String keyword) {
        List<User> users = keyword == null || keyword.isBlank()
                ? userRepository.findAll()
                : userRepository.findByEmailContainingIgnoreCaseOrNicknameContainingIgnoreCaseOrPhoneNumberContaining(
                        keyword, keyword, keyword
                );

        return users.stream()
                .map(user -> new UserSummaryResponse(user.getId(), user.getEmail(), user.getNickname(), user.getPhoneNumber(), user.getRole().name()))
                .toList();
    }
}
