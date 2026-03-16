package com.hana8.hanaro;

import com.hana8.hanaro.config.AppProperties;
import com.hana8.hanaro.config.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({SecurityProperties.class, AppProperties.class})
public class HanaroApplication {

    public static void main(String[] args) {
        SpringApplication.run(HanaroApplication.class, args);
    }
}
