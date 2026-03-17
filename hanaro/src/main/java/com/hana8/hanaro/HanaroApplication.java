package com.hana8.hanaro;

import com.hana8.hanaro.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
public class HanaroApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(HanaroApplication.class, args);
        log.debug("application started, beanDefinitionCount={}", ctx.getBeanDefinitionCount());
    }
}
