package com.hana8.hanaro.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana8.hanaro.dto.AuthResponseDTO;
import com.hana8.hanaro.dto.LoginRequest;
import com.hana8.hanaro.dto.SignUpRequestDTO;
import com.hana8.hanaro.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @Test
    void signUp() throws Exception {
        doNothing().when(authService).signUp(any());
        SignUpRequestDTO request = new SignUpRequestDTO("a@test.com", "12345678", "nick", "01012345678");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void signUpWithInvalidEmailAndPasswordReturnsBadRequest() throws Exception {
        SignUpRequestDTO request = new SignUpRequestDTO("invalid-email", "1234", "", "01012345678");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("올바른 이메일 형식이어야 합니다."))
                .andExpect(jsonPath("$.password").value("비밀번호는 8자 이상 30자 이하여야 합니다."))
                .andExpect(result -> org.assertj.core.api.Assertions.assertThat(result.getResponse().getContentAsString())
                        .contains("닉네임은 필수입니다."));
    }

    @Test
    void login() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(new AuthResponseDTO("token", "Bearer"));
        LoginRequest request = new LoginRequest("a@test.com", "12345678");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("token"));
    }
}
