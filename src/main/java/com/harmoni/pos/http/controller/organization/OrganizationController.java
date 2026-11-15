package com.harmoni.pos.http.controller.organization;

import com.harmoni.pos.business.service.organization.OrganizationService;
import com.harmoni.pos.http.response.RestAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the nested organization tree used by the unified
 * Brand / Chain / Store page.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    /**
     * Returns every brand with its chains and stores.
     *
     * @return the organization tree
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAnyAuthority('organization:read', 'employee:view', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> tree() {
        RestAPIResponse response = RestAPIResponse.builder()
                .data(organizationService.tree())
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
