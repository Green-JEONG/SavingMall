package com.hana8.hanaro;

import com.hana8.hanaro.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
public class HanaroApplication {

    public static void main(String[] args) {
        SpringApplication.run(HanaroApplication.class, args);
    }
}
