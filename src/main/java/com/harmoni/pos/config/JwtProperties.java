package com.harmoni.pos.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "harmoni.menu.jwt")
public class JwtProperties {
    private String secret;
    private long expiredTime;
}
