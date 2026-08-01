package com.harmoni.pos.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth.url")
public class AuthUrlProperties {
    private String register;
    private String delete;
    private String update;
}
