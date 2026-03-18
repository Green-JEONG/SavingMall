package com.hana8.hanaro.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hana8.hanaro.security.JwtAuthenticationFilter;
import com.hana8.hanaro.security.handler.CustomAccessDeniedHandler;
import com.hana8.hanaro.security.handler.CustomAuthenticationEntryPoint;
import com.hana8.hanaro.security.handler.LoginFailureHandler;
import com.hana8.hanaro.security.handler.LoginSuccessHandler;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

class CustomSecurityConfigTest {

    @Test
    void exposesSecurityBeans() throws Exception {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        AuthenticationConfiguration authenticationConfiguration = mock(AuthenticationConfiguration.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        CustomSecurityConfig config = new CustomSecurityConfig(
                mock(LoginSuccessHandler.class),
                mock(LoginFailureHandler.class),
                mock(CustomAuthenticationEntryPoint.class),
                mock(CustomAccessDeniedHandler.class),
                mock(JwtAuthenticationFilter.class)
        );

        assertThat(config.passwordEncoder().matches("password", config.passwordEncoder().encode("password"))).isTrue();
        assertThat(config.authenticationManager(authenticationConfiguration)).isSameAs(authenticationManager);
        assertThat(config.grantedAuthorityDefaults().getRolePrefix()).isEmpty();
    }
}
