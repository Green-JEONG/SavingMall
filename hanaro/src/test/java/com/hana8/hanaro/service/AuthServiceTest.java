package com.hana8.hanaro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.hana8.hanaro.common.enums.Role;
import com.hana8.hanaro.common.exception.BusinessException;
import com.hana8.hanaro.common.exception.ErrorCode;
import com.hana8.hanaro.common.logging.LogEventPublisher;
import com.hana8.hanaro.dto.AuthResponse;
import com.hana8.hanaro.dto.LoginRequest;
import com.hana8.hanaro.dto.SignUpRequest;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.repository.UserRepository;
import com.hana8.hanaro.security.JwtProvider;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private AccountService accountService;
    @Mock
    private LogEventPublisher logEventPublisher;

    @InjectMocks
    private AuthService authService;

    @Test
    void givenUniqueSignUpRequest_whenSignUp_thenUserIsSavedAndFreeAccountCreated() {
        SignUpRequest request = new SignUpRequest("user@test.com", "password", "tester", "01012345678");
        given(passwordEncoder.encode("password")).willReturn("encoded-password");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        authService.signUp(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getEmail()).isEqualTo("user@test.com");
        assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
        assertThat(savedUser.getNickname()).isEqualTo("tester");
        assertThat(savedUser.getPhoneNumber()).isEqualTo("01012345678");
        assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(savedUser.getCreatedAt()).isNotNull();
        verify(accountService).createFreeAccount(savedUser);
    }

    @Test
    void givenDuplicateEmail_whenSignUp_thenDuplicateEmailExceptionIsThrown() {
        SignUpRequest request = new SignUpRequest("user@test.com", "password", "tester", "01012345678");
        given(userRepository.existsByEmail("user@test.com")).willReturn(true);

        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    void givenValidCredentials_whenLogin_thenTokenIsReturned() {
        LoginRequest request = new LoginRequest("user@test.com", "password");
        User user = User.builder()
                .email("user@test.com")
                .password("encoded")
                .nickname("tester")
                .phoneNumber("01012345678")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        given(userRepository.findByEmail("user@test.com")).willReturn(java.util.Optional.of(user));
        given(jwtProvider.createToken("user@test.com", "ROLE_USER")).willReturn("access-token");

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void givenInvalidCredentials_whenLogin_thenInvalidCredentialExceptionIsThrown() {
        LoginRequest request = new LoginRequest("user@test.com", "wrong-password");
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_CREDENTIALS);
    }
}
