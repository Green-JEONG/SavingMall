package com.hana8.hanaro.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.security.SecurityScheme;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

class SwaggerConfigTest {

    @TempDir
    Path tempDir;

    @Test
    void createsOpenApiAndGroups() throws Exception {
        SwaggerConfig config = new SwaggerConfig();
        ReflectionTestUtils.setField(config, "uploadPath", Files.createDirectory(tempDir.resolve("upload")).toString());

        SecurityScheme scheme = config.openAPI().getComponents().getSecuritySchemes().get("bearerAuth");
        GroupedOpenApi adminApi = config.adminApi();
        GroupedOpenApi userApi = config.userApi();

        assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(adminApi.getGroup()).isEqualTo("ADMIN");
        assertThat(userApi.getGroup()).isEqualTo("USER");

        StaticWebApplicationContext context = new StaticWebApplicationContext();
        context.setServletContext(new MockServletContext());
        ResourceHandlerRegistry registry = new ResourceHandlerRegistry(context, context.getServletContext());
        config.addResourceHandlers(registry);
        assertThat(registry.hasMappingForPattern("/upload/**")).isTrue();
    }
}
