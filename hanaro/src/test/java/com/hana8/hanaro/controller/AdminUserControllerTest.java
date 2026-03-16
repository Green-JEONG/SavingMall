package com.hana8.hanaro.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hana8.hanaro.dto.SubscriptionResponse;
import com.hana8.hanaro.service.SubscriptionService;
import com.hana8.hanaro.dto.UserSummaryResponse;
import com.hana8.hanaro.service.UserService;
import com.hana8.hanaro.common.enums.SubscriptionStatus;
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
@AutoConfigureMockMvc(addFilters = false)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private SubscriptionService subscriptionService;

    @Test
    void users() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(new UserSummaryResponse(
                1L, "u@test.com", "nick", "01012345678", "ROLE_USER"
        )));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void userSubscriptions() throws Exception {
        when(subscriptionService.subscriptionsByUser(1L)).thenReturn(List.of(new SubscriptionResponse(
                1L, "상품", "123-4567-8901", SubscriptionStatus.ACTIVE,
                LocalDate.now(), LocalDate.now().plusMonths(6), BigDecimal.ZERO
        )));

        mockMvc.perform(get("/api/admin/users/1/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void processMaturity() throws Exception {
        when(subscriptionService.processMaturedSubscriptions()).thenReturn(2);

        mockMvc.perform(post("/api/admin/users/maturity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(2));
    }
}
