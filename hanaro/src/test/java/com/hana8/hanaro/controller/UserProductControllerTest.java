package com.hana8.hanaro.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hana8.hanaro.dto.ProductResponseDTO;
import com.hana8.hanaro.service.ProductService;
import com.hana8.hanaro.common.enums.ProductType;
import java.math.BigDecimal;
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
class UserProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void getAll() throws Exception {
        when(productService.getAll()).thenReturn(List.of(
                new ProductResponseDTO(1L, "p", ProductType.DEPOSIT, BigDecimal.TEN, null, 12,
                        BigDecimal.valueOf(3), BigDecimal.ONE, null)
        ));

        mockMvc.perform(get("/api/user/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void getOne() throws Exception {
        when(productService.getOne(1L)).thenReturn(new ProductResponseDTO(1L, "p", ProductType.DEPOSIT, BigDecimal.TEN,
                null, 12, BigDecimal.valueOf(3), BigDecimal.ONE, null));

        mockMvc.perform(get("/api/user/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
