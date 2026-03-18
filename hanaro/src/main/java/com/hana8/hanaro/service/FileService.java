package com.hana8.hanaro.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    private static final long MAX_FILE_SIZE_BYTES = 2L * 1024 * 1024;
    private static final long MAX_TOTAL_UPLOAD_SIZE_BYTES = 10L * 1024 * 1024;
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
            throw new IllegalArgumentException("파일은 1개당 최대 2MB까지 업로드할 수 있습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename == null ? "" : originalFilename);
        String dateDirectory = LocalDate.now().format(DATE_FORMATTER);
        Path basePath = Paths.get(isSecure ? securePath : uploadPath).toAbsolutePath().normalize();
        Path targetDirectory = basePath.resolve(dateDirectory).normalize();

        if (!targetDirectory.startsWith(basePath)) {
            throw new IllegalArgumentException("허용되지 않은 파일 경로입니다.");
        }

        try {
            Files.createDirectories(targetDirectory);
            Path savePath = resolveUniqueSavePath(targetDirectory, extension);
            file.transferTo(savePath);
            String storedFilename = savePath.getFileName().toString();
            return "/upload/" + dateDirectory + "/" + storedFilename;
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다.", e);
        }
    }

    public List<String> uploadMultiple(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일을 선택해 주세요.");
        }

        long totalSize = files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .mapToLong(MultipartFile::getSize)
                .sum();

        if (totalSize > MAX_TOTAL_UPLOAD_SIZE_BYTES) {
            throw new IllegalArgumentException("전체 업로드 용량은 최대 10MB까지 가능합니다.");
        }

        return files.stream()
                .map(this::uploadRequired)
                .toList();
    }

    public String uploadRequired(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("업로드할 파일을 선택해 주세요.");
        }
        return upload(file);
    }

    private Path resolveUniqueSavePath(Path targetDirectory, String extension) throws IOException {
        Path savePath;
        do {
            savePath = targetDirectory.resolve(UUID.randomUUID() + extension).normalize();
        } while (Files.exists(savePath));
        return savePath;
    }

    private String extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return filename.substring(dotIndex);
    }
}
