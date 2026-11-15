package com.harmoni.pos.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the menu service.
 *
 * <p>The gateway authenticates the caller and forwards the identity headers;
 * {@link HeaderAuthoritiesFilter} turns them into a {@code SecurityContext}.
 * Method security then enforces the {@code @PreAuthorize} rules on the
 * controllers. Internal service endpoints are allowed without a bearer token
 * because they are only reachable on the internal network.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   HeaderAuthoritiesFilter headerAuthoritiesFilter)
            throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/internal/**", "/swagger-ui/**", "/v3/api-docs/**",
                                "/actuator/**", "/error").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(headerAuthoritiesFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
