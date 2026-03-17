package com.hana8.hanaro.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana8.hanaro.dto.ProductRequestDTO;
import com.hana8.hanaro.dto.ProductResponseDTO;
import com.hana8.hanaro.service.ProductService;
import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.SavingsCycle;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "ADMIN")
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductService productService;

    @Test
    void getAll() throws Exception {
        when(productService.getAll()).thenReturn(List.of(productResponse()));

        mockMvc.perform(get("/api/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void create() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO("상품", ProductType.SAVINGS, BigDecimal.valueOf(10000),
                SavingsCycle.MONTHLY, 12, BigDecimal.valueOf(4.0), BigDecimal.valueOf(1.5));
        when(productService.create(any(ProductRequestDTO.class), any())).thenReturn(productResponse());

        MockMultipartFile requestPart = new MockMultipartFile(
                "request", "request", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        mockMvc.perform(multipart("/api/admin/products").file(requestPart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void getOne() throws Exception {
        when(productService.getOne(1L)).thenReturn(productResponse());

        mockMvc.perform(get("/api/admin/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void update() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO("상품", ProductType.SAVINGS, BigDecimal.valueOf(10000),
                SavingsCycle.MONTHLY, 12, BigDecimal.valueOf(4.0), BigDecimal.valueOf(1.5));
        when(productService.update(eq(1L), any(ProductRequestDTO.class), any())).thenReturn(productResponse());

        MockMultipartFile requestPart = new MockMultipartFile(
                "request", "request", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        MockMultipartHttpServletRequestBuilder builder = multipart("/api/admin/products/1").file(requestPart);
        builder.with(req -> {
            req.setMethod("PUT");
            return req;
        });

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void deleteProduct() throws Exception {
        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/api/admin/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    private ProductResponseDTO productResponse() {
        return new ProductResponseDTO(1L, "상품", ProductType.SAVINGS, BigDecimal.valueOf(10000), SavingsCycle.MONTHLY,
                12, BigDecimal.valueOf(4.0), BigDecimal.valueOf(1.5), null);
    }
}
