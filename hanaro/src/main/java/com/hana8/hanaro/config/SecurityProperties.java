package com.hana8.hanaro.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private Admin admin = new Admin();
    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    public static class Admin {
        private String username;
        private String password;
        private String nickname;
    }

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long accessTokenExpirationMs;
    }
}
