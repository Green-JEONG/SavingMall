package com.hana8.hanaro.config;

import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/swagger-ui/index/html", "/swagger-ui/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDirectory = Paths.get(uploadPath).toAbsolutePath().normalize();
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(toDirectoryResourceLocation(uploadDirectory));
    }

    private String toDirectoryResourceLocation(Path directory) {
        String resourceLocation = directory.toUri().toString();
        return resourceLocation.endsWith("/") ? resourceLocation : resourceLocation + "/";
    }

    @Bean
    public OperationCustomizer globalResponseOperationCustomizer() {
        return (operation, handlerMethod) -> {
            ApiResponses responses = operation.getResponses();
            if (responses == null) {
                responses = new ApiResponses();
                operation.setResponses(responses);
            }

            addResponseIfAbsent(responses, "301", "리다이렉트되었습니다.");
            addResponseIfAbsent(responses, "401", "인증이 필요합니다.");
            addResponseIfAbsent(responses, "404", "요청한 리소스를 찾을 수 없습니다.");
            addResponseIfAbsent(responses, "503", "서비스를 일시적으로 사용할 수 없습니다.");

            return operation;
        };
    }

    private void addResponseIfAbsent(ApiResponses responses, String statusCode, String description) {
        if (!responses.containsKey(statusCode)) {
            responses.addApiResponse(statusCode, new ApiResponse().description(description));
        }
    }
}
