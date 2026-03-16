package com.hana8.hanaro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.hana8.hanaro.common.enums.Role;
import com.hana8.hanaro.common.exception.BusinessException;
import com.hana8.hanaro.common.exception.ErrorCode;
import com.hana8.hanaro.dto.UserSummaryResponse;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void givenAuthenticatedUserEmail_whenCurrentUser_thenUserIsReturned() {
        User user = user("user@test.com", "tester", "01012345678");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user@test.com", "password", List.of())
        );
        given(userRepository.findByEmail("user@test.com")).willReturn(Optional.of(user));

        User result = userService.currentUser();

        assertThat(result).isEqualTo(user);
    }

    @Test
    void givenUnknownAuthenticatedUserEmail_whenCurrentUser_thenUserNotFoundExceptionIsThrown() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("missing@test.com", "password", List.of())
        );
        given(userRepository.findByEmail("missing@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.currentUser())
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void givenUsers_whenGetAllUsers_thenSummaryListIsReturned() {
        given(userRepository.findAll()).willReturn(List.of(
                user("user1@test.com", "tester1", "01012340001"),
                user("user2@test.com", "tester2", "01012340002")
        ));

        List<UserSummaryResponse> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).email()).isEqualTo("user1@test.com");
        assertThat(result.get(1).role()).isEqualTo("ROLE_USER");
    }

    private User user(String email, String nickname, String phoneNumber) {
        return User.builder()
                .id(1L)
                .email(email)
                .password("password")
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
