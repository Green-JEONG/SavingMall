package com.hana8.hanaro;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.mockito.MockedStatic;

@SpringBootTest
class HanaroApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainRunsSpringApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
            when(context.getBeanDefinitionCount()).thenReturn(1);

            springApplication.when(() -> SpringApplication.run(HanaroApplication.class, new String[]{}))
                    .thenReturn(context);

            HanaroApplication.main(new String[]{});

            springApplication.verify(() -> SpringApplication.run(HanaroApplication.class, new String[]{}));
        }
    }

}
