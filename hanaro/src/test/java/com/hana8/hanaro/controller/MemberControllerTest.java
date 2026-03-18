package com.hana8.hanaro.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hana8.hanaro.common.enums.SubscriptionStatus;
import com.hana8.hanaro.dto.SubscriptionResponseDTO;
import com.hana8.hanaro.dto.UserSummaryResponseDTO;
import com.hana8.hanaro.service.SubscriptionService;
import com.hana8.hanaro.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private SubscriptionService subscriptionService;

    @Test
    void meIsAccessibleToUserRole() throws Exception {
        when(userService.currentUserSummary()).thenReturn(
                new UserSummaryResponseDTO(1L, "user@test.com", "nick", "01012345678", "ROLE_USER")
        );

        mockMvc.perform(get("/api/members/me").with(user("user@test.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("user@test.com"));
    }

    @Test
    void membersIsAccessibleToAdminRole() throws Exception {
        when(userService.getAllUsers("nick")).thenReturn(List.of(
                new UserSummaryResponseDTO(1L, "user@test.com", "nick", "01012345678", "ROLE_USER")
        ));

        mockMvc.perform(get("/api/members").param("keyword", "nick").with(user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nickname").value("nick"));
    }

    @Test
    void memberSubscriptionsIsAccessibleToAdminRole() throws Exception {
        when(subscriptionService.subscriptionsByUser(1L)).thenReturn(List.of(
                new SubscriptionResponseDTO(
                        1L,
                        "상품",
                        "123-4567-8901",
                        SubscriptionStatus.ACTIVE,
                        LocalDate.now(),
                        LocalDate.now().plusMonths(12),
                        BigDecimal.ZERO
                )
        ));

        mockMvc.perform(get("/api/members/1/subscriptions").with(user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void processMaturityIsAccessibleToAdminRole() throws Exception {
        when(subscriptionService.processMaturedSubscriptions()).thenReturn(2);

        mockMvc.perform(post("/api/members/maturity").with(user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(2));
    }

    @Test
    void adminOnlyEndpointIsForbiddenForUserRole() throws Exception {
        mockMvc.perform(get("/api/members").with(user("user@test.com").roles("USER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access Denied"));
    }
}
