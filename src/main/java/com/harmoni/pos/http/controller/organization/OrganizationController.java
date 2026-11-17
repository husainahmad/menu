package com.harmoni.pos.http.controller.organization;

import com.harmoni.pos.business.service.organization.OrganizationService;
import com.harmoni.pos.component.CallerScope;
import com.harmoni.pos.component.CallerScopeContext;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.OrganizationTreeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
                .data(filterTree(CallerScopeContext.current(), organizationService.tree()))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private OrganizationTreeDto filterTree(CallerScope scope, OrganizationTreeDto tree) {
        if (scope.isGlobal() || tree == null) {
            return tree;
        }
        List<OrganizationTreeDto.BrandNode> brands = new ArrayList<>();
        for (OrganizationTreeDto.BrandNode brand : tree.getBrands()) {
            if (!scope.canAccessBrand(brand.getId())) {
                continue;
            }
            List<OrganizationTreeDto.ChainNode> chains = new ArrayList<>();
            int brandStoreCount = 0;
            for (OrganizationTreeDto.ChainNode chain : brand.getChains()) {
                if (!scope.canAccessChain(chain.getId())) {
                    continue;
                }
                List<OrganizationTreeDto.StoreNode> stores = chain.getStores().stream()
                        .filter(store -> scope.canAccessStore(store.getId()))
                        .toList();
                chain.setStores(new ArrayList<>(stores));
                chain.setStoreCount(stores.size());
                brandStoreCount += stores.size();
                chains.add(chain);
            }
            brand.setChains(chains);
            brand.setChainCount(chains.size());
            brand.setStoreCount(brandStoreCount);
            brands.add(brand);
        }
        tree.setBrands(brands);
        return tree;
    }
}
