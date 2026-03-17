package com.hana8.hanaro.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana8.hanaro.dto.SubscribeRequestDTO;
import com.hana8.hanaro.dto.SubscriptionResponseDTO;
import com.hana8.hanaro.dto.TransferRequestDTO;
import com.hana8.hanaro.service.SubscriptionService;
import com.hana8.hanaro.common.enums.SubscriptionStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "USER")
class UserSubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private SubscriptionService subscriptionService;

    @Test
    void subscribe() throws Exception {
        when(subscriptionService.subscribe(any())).thenReturn(new SubscriptionResponseDTO(
                1L, "상품", "123-4567-8901", SubscriptionStatus.ACTIVE,
                LocalDate.now(), LocalDate.now().plusMonths(12), BigDecimal.ZERO
        ));

        mockMvc.perform(post("/api/user/subscriptions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new SubscribeRequestDTO(1L, "12345678901"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void mySubscriptions() throws Exception {
        when(subscriptionService.mySubscriptions()).thenReturn(List.of(new SubscriptionResponseDTO(
                1L, "상품", "123-4567-8901", SubscriptionStatus.ACTIVE,
                LocalDate.now(), LocalDate.now().plusMonths(12), BigDecimal.ZERO
        )));

        mockMvc.perform(get("/api/user/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void terminate() throws Exception {
        when(subscriptionService.terminate(1L)).thenReturn(new SubscriptionResponseDTO(
                1L, "상품", "123-4567-8901", SubscriptionStatus.TERMINATED,
                LocalDate.now(), LocalDate.now().plusMonths(12), BigDecimal.valueOf(100)
        ));

        mockMvc.perform(post("/api/user/subscriptions/1/terminate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("TERMINATED"));
    }

    @Test
    void transfer() throws Exception {
        doNothing().when(subscriptionService).transfer(any(TransferRequestDTO.class));

        mockMvc.perform(post("/api/user/subscriptions/transfer")
                        .contentType("application/json")
                        .content("{\"subscriptionId\":1,\"amount\":1000,\"fromAccountNumber\":\"12345678901\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
