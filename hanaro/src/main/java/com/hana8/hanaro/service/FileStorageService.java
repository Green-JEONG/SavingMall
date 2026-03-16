package com.hana8.hanaro.service;

import com.hana8.hanaro.config.AppProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final AppProperties appProperties;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String datePath = LocalDate.now().format(DATE_FORMATTER);
        Path directory = Paths.get(appProperties.getRootPath(), datePath);

        try {
            Files.createDirectories(directory);
            String originalFilename = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String extension = extractExtension(originalFilename);
            String storedFilename = UUID.randomUUID() + extension;
            Path savePath = directory.resolve(storedFilename);
            file.transferTo(savePath);
            return "/upload/" + datePath + "/" + storedFilename;
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다.", e);
        }
    }

    private String extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return filename.substring(dotIndex);
    }
}
