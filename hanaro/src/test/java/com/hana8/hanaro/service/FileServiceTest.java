package com.hana8.hanaro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

class FileServiceTest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @TempDir
    Path tempDir;

    @Test
    void uploadStoresFilesUnderTodayDirectoryWithUniqueName() throws IOException {
        FileService fileService = new FileService();
        ReflectionTestUtils.setField(fileService, "uploadPath", tempDir.toString());
        ReflectionTestUtils.setField(fileService, "securePath", tempDir.resolve("secure").toString());

        MockMultipartFile firstFile = new MockMultipartFile(
                "image", "sample.png", "image/png", "first-image".getBytes()
        );
        MockMultipartFile secondFile = new MockMultipartFile(
                "image", "sample.png", "image/png", "second-image".getBytes()
        );

        String firstSavedPath = fileService.upload(firstFile);
        String secondSavedPath = fileService.upload(secondFile);

        String todayDirectory = LocalDate.now().format(DATE_FORMATTER);
        Path targetDirectory = tempDir.resolve(todayDirectory);

        assertThat(firstSavedPath).startsWith("/upload/" + todayDirectory + "/").endsWith(".png");
        assertThat(secondSavedPath).startsWith("/upload/" + todayDirectory + "/").endsWith(".png");
        assertThat(firstSavedPath).isNotEqualTo(secondSavedPath);
        assertThat(Files.isDirectory(targetDirectory)).isTrue();
        try (Stream<Path> savedFiles = Files.list(targetDirectory)) {
            assertThat(savedFiles.count()).isEqualTo(2);
        }
    }

    @Test
    void uploadReturnsNullWhenFileIsMissingOrEmpty() {
        FileService fileService = new FileService();
        ReflectionTestUtils.setField(fileService, "uploadPath", tempDir.toString());
        ReflectionTestUtils.setField(fileService, "securePath", tempDir.resolve("secure").toString());

        MockMultipartFile emptyFile = new MockMultipartFile(
                "image", "empty.png", "image/png", new byte[0]
        );

        assertThat(fileService.upload(null)).isNull();
        assertThat(fileService.upload(emptyFile)).isNull();
    }

    @Test
    void uploadRejectsFileLargerThanTwoMegabytes() {
        FileService fileService = new FileService();
        ReflectionTestUtils.setField(fileService, "uploadPath", tempDir.toString());
        ReflectionTestUtils.setField(fileService, "securePath", tempDir.resolve("secure").toString());

        MockMultipartFile oversizedFile = new MockMultipartFile(
                "image", "large.png", "image/png", new byte[2 * 1024 * 1024 + 1]
        );

        assertThatThrownBy(() -> fileService.upload(oversizedFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파일 하나의 최대 크기는 2MB입니다.");
    }
}
