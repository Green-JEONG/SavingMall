package com.hana8.hanaro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwaggerUiController {

    private final String swaggerConfigUrl;

    public SwaggerUiController(@Value("${springdoc.api-docs.path:/v3/api-docs}") String apiDocsPath) {
        this.swaggerConfigUrl = apiDocsPath + "/swagger-config";
    }

    @GetMapping(value = "/swagger-ui/swagger-initializer.js", produces = "application/javascript")
    public String swaggerInitializer() {
        return """
                window.onload = async function() {
                  const applyCustomStyles = function() {
                    if (document.getElementById('hanaro-swagger-style')) {
                      return;
                    }

                    const style = document.createElement('style');
                    style.id = 'hanaro-swagger-style';
                    style.textContent = `
                      .swagger-ui textarea,
                      .swagger-ui .body-param__text,
                      .swagger-ui textarea.curl {
                        min-height: 24rem !important;
                        font-family: Menlo, Monaco, Consolas, 'Courier New', monospace !important;
                        line-height: 1.5 !important;
                      }

                      .swagger-ui input[type=text],
                      .swagger-ui input[type=password],
                      .swagger-ui input[type=search],
                      .swagger-ui input[type=email],
                      .swagger-ui input[type=url] {
                        width: 100%% !important;
                        max-width: none !important;
                      }

                      .swagger-ui .parameters-col_description,
                      .swagger-ui .opblock-description-wrapper,
                      .swagger-ui .opblock-external-docs-wrapper,
                      .swagger-ui .opblock-title_normal {
                        width: 100%% !important;
                        max-width: none !important;
                      }

                      .swagger-ui .model-box,
                      .swagger-ui .highlight-code,
                      .swagger-ui .microlight,
                      .swagger-ui .renderedMarkdown,
                      .swagger-ui .response-col_description__inner {
                        max-width: none !important;
                      }

                      .swagger-ui .opblock-body pre,
                      .swagger-ui .responses-inner pre,
                      .swagger-ui .highlight-code > .microlight {
                        max-height: 32rem !important;
                      }
                    `;
                    document.head.appendChild(style);
                  };

                  applyCustomStyles();

                  const response = await fetch('%s', { credentials: 'same-origin' });
                  const config = await response.json();

                  window.ui = SwaggerUIBundle({
                    ...config,
                    dom_id: '#swagger-ui',
                    deepLinking: true,
                    presets: [
                      SwaggerUIBundle.presets.apis,
                      SwaggerUIStandalonePreset
                    ],
                    plugins: [
                      SwaggerUIBundle.plugins.DownloadUrl
                    ],
                    layout: 'StandaloneLayout'
                  });

                  const observer = new MutationObserver(applyCustomStyles);
                  observer.observe(document.body, { childList: true, subtree: true });
                };
                """.formatted(swaggerConfigUrl);
    }
}
