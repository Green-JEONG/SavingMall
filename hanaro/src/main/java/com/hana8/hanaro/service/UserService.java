package com.hana8.hanaro.service;

import com.hana8.hanaro.dto.UserSummaryResponse;
import com.hana8.hanaro.repository.UserRepository;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.security.SecurityUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User currentUser() {
        return userRepository.findByEmail(SecurityUtil.currentUserEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
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
