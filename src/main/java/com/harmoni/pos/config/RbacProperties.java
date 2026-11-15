package com.harmoni.pos.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RBAC enforcement settings for the menu service.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "harmoni.menu.rbac")
public class RbacProperties {

    /**
     * When {@code true}, requests must carry the permission required by their
     * resource in the {@code X-Permissions} header injected by the gateway.
     * Disabled by default so the service keeps working while RBAC rolls out.
     */
    private boolean enforce = false;
}
