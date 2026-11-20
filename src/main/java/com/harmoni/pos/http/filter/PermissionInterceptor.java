package com.harmoni.pos.http.filter;

import com.harmoni.pos.config.RbacProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Enforces the module permission required by a request.
 *
 * <p>The gateway validates the JWT and forwards the granted permissions in the
 * {@code X-Permissions} header. This interceptor maps the request path to a
 * module and the HTTP method to an action, then requires
 * {@code module:action} to be present. Enforcement is opt-in through
 * {@code harmoni.menu.rbac.enforce}.</p>
 */
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private static final String HEADER_PERMISSIONS = "X-Permissions";

    private static final Map<String, String> MODULE_BY_PREFIX = new LinkedHashMap<>();

    static {
        MODULE_BY_PREFIX.put("/api/v1/user", "user");
        MODULE_BY_PREFIX.put("/api/v1/product", "catalog");
        MODULE_BY_PREFIX.put("/api/v1/category", "catalog");
        MODULE_BY_PREFIX.put("/api/v1/customization", "catalog");
        MODULE_BY_PREFIX.put("/api/v1/promotion", "catalog");
        MODULE_BY_PREFIX.put("/api/v1/sku", "catalog");
        MODULE_BY_PREFIX.put("/api/v1/skutier", "catalog");
        MODULE_BY_PREFIX.put("/api/v1/image", "catalog");
        MODULE_BY_PREFIX.put("/api/v1/store", "organization");
        MODULE_BY_PREFIX.put("/api/v1/chain", "organization");
        MODULE_BY_PREFIX.put("/api/v1/brand", "organization");
        MODULE_BY_PREFIX.put("/api/v1/tier", "organization");
        MODULE_BY_PREFIX.put("/api/v1/organization", "organization");
        MODULE_BY_PREFIX.put("/api/v1/table", "operation");
        MODULE_BY_PREFIX.put("/api/v1/floor", "operation");
        MODULE_BY_PREFIX.put("/api/v1/service", "operation");
        MODULE_BY_PREFIX.put("/api/v1/subservice", "operation");
        MODULE_BY_PREFIX.put("/api/v1/storeservicetype", "operation");
        MODULE_BY_PREFIX.put("/api/v1/stores", "operation");
        MODULE_BY_PREFIX.put("/api/v1/kitchen-stations", "operation");
        MODULE_BY_PREFIX.put("/api/v1/devices", "operation");
        MODULE_BY_PREFIX.put("/api/v1/kitchen-printers", "operation");
    }

    private final RbacProperties rbacProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (!rbacProperties.isEnforce()) {
            return true;
        }
        String path = request.getRequestURI();
        if (path.startsWith("/internal/") || path.startsWith("/actuator") || path.startsWith("/error")) {
            return true;
        }
        String module = moduleFor(path);
        if (module == null) {
            return true;
        }
        String required = module + ":" + actionFor(request.getMethod());
        if (hasPermission(request.getHeader(HEADER_PERMISSIONS), required)) {
            return true;
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing permission: " + required);
        return false;
    }

    private String moduleFor(String path) {
        return MODULE_BY_PREFIX.entrySet().stream()
                .filter(entry -> path.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private String actionFor(String method) {
        if ("GET".equalsIgnoreCase(method)) {
            return "read";
        }
        if ("DELETE".equalsIgnoreCase(method)) {
            return "delete";
        }
        return "write";
    }

    private boolean hasPermission(String header, String required) {
        if (header == null || header.isBlank()) {
            return false;
        }
        return Arrays.stream(header.split(","))
                .map(String::trim)
                .anyMatch(required::equals);
    }
}
