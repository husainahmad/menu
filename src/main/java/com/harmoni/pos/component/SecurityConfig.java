package com.harmoni.pos.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

/**
 * Configuration class for customizing web security settings.
 */
@Configuration
public class SecurityConfig  {

    /**
     * Configures web security to ignore requests matching the specified patterns.
     *
     * @return the WebSecurityCustomizer bean
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/api/**");
    }
}
