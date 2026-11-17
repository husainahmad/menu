package com.harmoni.pos.http.controller.organization;

import com.harmoni.pos.component.CallerScope;
import com.harmoni.pos.component.CallerScopeContext;
import com.harmoni.pos.http.response.RestAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal endpoint that exposes the caller's expanded store scope to sibling
 * services (for example the order service, which only stores store ids).
 *
 * <p>It is reachable only on the internal network and returns nothing but the
 * caller's own scope, so it cannot be used to discover other tenants.</p>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/v1/menu/scope")
public class InternalScopeController {

    /**
     * Returns the store ids the caller may access.
     *
     * @return {@code { global, storeIds }} where {@code global} means unrestricted
     */
    @GetMapping("/stores")
    public ResponseEntity<RestAPIResponse> stores() {
        CallerScope scope = CallerScopeContext.current();
        Map<String, Object> data = new HashMap<>();
        data.put("global", scope.isGlobal());
        data.put("storeIds", new ArrayList<>(scope.getStoreIds()));
        RestAPIResponse response = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(data)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
