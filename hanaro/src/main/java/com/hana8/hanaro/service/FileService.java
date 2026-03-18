package com.hana8.hanaro.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    private static final long MAX_FILE_SIZE_BYTES = 2L * 1024 * 1024;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.secure}")
    private String securePath;

    public String upload(MultipartFile file) {
        return upload(file, false);
    }

    public String upload(MultipartFile file, boolean isSecure) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("파일 하나의 최대 크기는 2MB입니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename == null ? "" : originalFilename);
        String dateDirectory = LocalDate.now().format(DATE_FORMATTER);
        String storedFilename = UUID.randomUUID() + extension;

        Path basePath = Paths.get(isSecure ? securePath : uploadPath).toAbsolutePath().normalize();
        Path targetDirectory = basePath.resolve(dateDirectory).normalize();

        if (!targetDirectory.startsWith(basePath)) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }

        try {
            Files.createDirectories(targetDirectory);
            Path savePath = targetDirectory.resolve(storedFilename);
            file.transferTo(savePath);
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다.", e);
        }

        return "/upload/" + dateDirectory + "/" + storedFilename;
    }

    private String extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return filename.substring(dotIndex);
    }
}
