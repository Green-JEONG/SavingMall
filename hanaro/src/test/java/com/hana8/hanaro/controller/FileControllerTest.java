package com.hana8.hanaro.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hana8.hanaro.service.FileService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "ADMIN")
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileService fileService;

    @Test
    void upload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "image".getBytes());
        when(fileService.uploadRequired(any())).thenReturn("/upload/20260318/test.png");

        mockMvc.perform(multipart("/api/admin/files/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("/upload/20260318/test.png"));
    }

    @Test
    void uploadMultiple() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("files", "a.png", "image/png", "a".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "b.png", "image/png", "b".getBytes());
        when(fileService.uploadMultiple(any())).thenReturn(List.of(
                "/upload/20260318/a.png",
                "/upload/20260318/b.png"
        ));

        mockMvc.perform(multipart("/api/admin/files/upload/multiple").file(file1).file(file2))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"/upload/20260318/a.png\",\"/upload/20260318/b.png\"]"));
    }
}
