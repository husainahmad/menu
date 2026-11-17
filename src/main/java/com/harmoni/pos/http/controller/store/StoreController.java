package com.harmoni.pos.http.controller.store;

import org.springframework.security.access.prepost.PreAuthorize;

import com.harmoni.pos.business.service.store.StoreService;
import com.harmoni.pos.component.CallerScope;
import com.harmoni.pos.component.CallerScopeContext;
import com.harmoni.pos.exception.ScopeAccessDeniedException;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.dto.StoreDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Store entities.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/store")
@Slf4j
@PreAuthorize("hasAnyAuthority('organization:read', 'rbac:manage')")
public class StoreController {

    private final StoreService storeService;

    /**
     * Creates a new Store.
     *
     * @param storeDto the store data transfer object
     * @return ResponseEntity with creation status
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('organization:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody StoreDto storeDto) {
        int id = storeService.create(storeDto);
        log.debug("store created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

    /**
     * Retrieves paginated list of Stores by Chain ID.
     *
     * @param chainId the chain ID
     * @param page the page number
     * @param size the page size
     * @param search the search term
     * @return ResponseEntity containing paginated stores
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('menu:view', 'catalog:read', 'organization:read', 'order:view', 'terminal:view', 'availability:view', 'employee:view', 'user:read', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> list(@RequestParam(name = "chainId") Integer chainId,
                                                @RequestParam(name = "page") int page,
                                                @RequestParam(name = "size") int size,
                                                @RequestParam(name = "search") String search) {
        CallerScope scope = CallerScopeContext.current();
        if (!scope.canAccessChain(chainId)) {
            throw new ScopeAccessDeniedException("Chain " + chainId + " is outside the caller's scope");
        }
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(filterStores(scope, storeService.getAllStoresByChainIdPaginated(chainId, page, size, search)))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Deletes a Store by its ID.
     *
     * @param id the store ID
     * @return ResponseEntity with deletion status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('organization:delete', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {
        CallerScopeContext.current().requireStore(id);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(storeService.delete(id))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Updates a Store by its ID.
     *
     * @param id the store ID
     * @param storeDto the store data transfer object
     * @return ResponseEntity with update status
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('organization:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> update(@PathVariable Integer id, @Valid @RequestBody StoreDto storeDto) {
        CallerScopeContext.current().requireStore(id);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(storeService.update(id, storeDto))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves a Store by its ID.
     *
     * @param id the store ID
     * @return ResponseEntity containing the store data
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {
        CallerScopeContext.current().requireStore(id);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(storeService.get(id))
                .httpStatus(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> filterStores(CallerScope scope, Map<String, Object> page) {
        if (scope.isGlobal() || page == null) {
            return page;
        }
        Object data = page.get("data");
        if (!(data instanceof List<?> rows)) {
            return page;
        }
        List<Store> allowed = scope.filterStores((List<Store>) rows);
        if (allowed.size() == rows.size()) {
            return page;
        }
        Map<String, Object> filtered = new HashMap<>(page);
        filtered.put("data", allowed);
        filtered.put("total", allowed.size());
        filtered.put("page", 1);
        return filtered;
    }

}
