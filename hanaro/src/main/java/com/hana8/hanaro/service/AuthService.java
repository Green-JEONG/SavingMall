package com.hana8.hanaro.service;

import com.hana8.hanaro.common.enums.Role;
import com.hana8.hanaro.common.logging.LogEventPublisher;
import com.hana8.hanaro.dto.AuthResponseDTO;
import com.hana8.hanaro.dto.LoginRequest;
import com.hana8.hanaro.dto.SignUpRequestDTO;
import com.hana8.hanaro.entity.User;
import com.hana8.hanaro.mapper.AuthMapper;
import com.hana8.hanaro.repository.UserRepository;
import com.hana8.hanaro.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AccountService accountService;
    private final LogEventPublisher logEventPublisher;

    @Transactional
    public void signUp(SignUpRequestDTO request) {
        validateDuplication(request);

        User user = AuthMapper.toUser(request, passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);
        accountService.createFreeAccount(saved);
        logEventPublisher.user("회원가입: " + saved.getEmail());
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        String token = jwtUtil.createAccessToken(user.getEmail(), user.getRole().name());
        logEventPublisher.user("로그인: " + user.getEmail());
        return AuthMapper.toAuthResponseDTO(token);
    }

    private void validateDuplication(SignUpRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByNickname(request.nickname())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 전화번호입니다.");
        }
    }
}
