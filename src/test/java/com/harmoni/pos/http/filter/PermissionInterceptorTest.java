package com.harmoni.pos.http.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.harmoni.pos.config.RbacProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class PermissionInterceptorTest {

    private RbacProperties properties;
    private PermissionInterceptor interceptor;

    @BeforeEach
    void setUp() {
        properties = new RbacProperties();
        interceptor = new PermissionInterceptor(properties);
    }

    @Test
    void allowsEverything_WhenEnforcementIsDisabled() throws Exception {
        properties.setEnforce(false);
        MockHttpServletRequest request = request("GET", "/api/v1/product");

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void denies_WhenPermissionHeaderIsMissing() throws Exception {
        properties.setEnforce(true);
        MockHttpServletRequest request = request("GET", "/api/v1/product");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(403, response.getStatus());
    }

    @Test
    void allows_WhenRequiredPermissionIsPresent() throws Exception {
        properties.setEnforce(true);
        MockHttpServletRequest request = request("GET", "/api/v1/product");
        request.addHeader("X-Permissions", "catalog:read,report:read");

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void writeRequiresWritePermission() throws Exception {
        properties.setEnforce(true);
        MockHttpServletRequest request = request("POST", "/api/v1/product");
        request.addHeader("X-Permissions", "catalog:read");

        assertFalse(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));

        request = request("POST", "/api/v1/product");
        request.addHeader("X-Permissions", "catalog:write");
        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void internalEndpointsAreExempt() throws Exception {
        properties.setEnforce(true);
        MockHttpServletRequest request = request("GET", "/internal/v1/menu/products");

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    private MockHttpServletRequest request(String method, String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        request.setRequestURI(uri);
        return request;
    }
}
