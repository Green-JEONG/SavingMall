package com.hana8.hanaro.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SwaggerUiControllerTest {

    @Test
    void returnsInitializerScriptWithConfiguredApiDocsPath() {
        SwaggerUiController controller = new SwaggerUiController("/hana8/api-docs");

        String script = controller.swaggerInitializer();

        assertThat(script).contains("/hana8/api-docs/swagger-config");
        assertThat(script).contains("SwaggerUIBundle");
    }
}
