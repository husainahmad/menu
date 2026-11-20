package com.harmoni.pos.http.controller.user;

import com.harmoni.pos.business.service.auth.AuthService;
import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.component.CallerScope;
import com.harmoni.pos.component.CallerScopeContext;
import com.harmoni.pos.exception.ScopeAccessDeniedException;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.User;
import com.harmoni.pos.menu.model.dto.UserDto;
import com.harmoni.pos.menu.model.dto.edit.UserEditDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing User entities.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * Creates a new User.
     *
     * @param userDto the user data transfer object
     * @param username the username
     * @return ResponseEntity with creation status
     * @throws Exception if an error occurs during creation
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('employee:edit', 'user:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> createUser(@Valid @RequestBody UserDto userDto,
                                                      @RequestHeader("X-Username") String username,
                                                      @RequestHeader(value = "Authorization", required = false) String authorization) throws Exception {
        CallerScopeContext.current().requireStore(userDto.getStoreId());
        int id = this.userService.insert(authorization, userDto);
        log.debug("tier created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

    /**
     * Retrieves Users by Chain ID with pagination and search.
     *
     * @param chainId the chain ID
     * @param page the page number
     * @param size the page size
     * @param search the search term
     * @return ResponseEntity containing users for the chain
     */
    @GetMapping("/chain/{chainId}")
    @PreAuthorize("hasAnyAuthority('employee:view', 'user:read', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> getByCategoryBrand(@PathVariable Integer chainId,
                                                              @RequestParam(name = "page") int page,
                                                              @RequestParam(name = "size") int size,
                                                              @RequestParam(name = "search") String search) {
        if (!CallerScopeContext.current().canAccessChain(chainId)) {
            throw new ScopeAccessDeniedException("Chain " + chainId + " is outside the caller's scope");
        }

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.userService.selectByChainId(chainId, page, size, search))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves a page of employees across the stores in the caller's scope.
     *
     * <p>When {@code storeIds} is omitted the caller's own scope is used: a
     * chain-scoped operator sees every store of the chain, a brand-scoped
     * operator every store of the brand, and a global operator every user.
     * This keeps the list correct even when the dashboard cannot load the
     * organisation tree (for example a role without {@code organization:read}).</p>
     *
     * @param storeIds  optional comma-separated store ids to narrow the page
     * @param page      1-based page number
     * @param size      page size
     * @param search    optional name/code search term
     * @param roleId    optional role filter
     * @param scopeType optional scope filter (BRAND, CHAIN, STORE)
     * @return a page of users with pagination metadata
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyAuthority('employee:view', 'user:read', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> getPage(@RequestParam(name = "storeIds", required = false) String storeIds,
                                                   @RequestParam(name = "page", defaultValue = "1") int page,
                                                   @RequestParam(name = "size", defaultValue = "10") int size,
                                                   @RequestParam(name = "search", required = false, defaultValue = "") String search,
                                                   @RequestParam(name = "roleId", required = false) String roleId,
                                                   @RequestParam(name = "scopeType", required = false) String scopeType,
                                                   @RequestHeader(value = "Authorization", required = false) String authorization) {

        CallerScope callerScope = CallerScopeContext.current();
        List<Integer> requestedStoreIds = parseStoreIds(storeIds);
        List<Integer> allowedStoreIds;
        if (requestedStoreIds.isEmpty()) {
            // No explicit selection: fall back to the caller's expanded scope.
            // A null list means "no store filter" and is only used for global
            // operators, who may see every user.
            allowedStoreIds = callerScope.isGlobal()
                    ? null : new ArrayList<>(callerScope.getStoreIds());
        } else {
            allowedStoreIds = callerScope.filterStoreIds(requestedStoreIds);
        }
        if (allowedStoreIds != null && allowedStoreIds.isEmpty()) {
            RestAPIResponse empty = RestAPIResponse.builder()
                    .httpStatus(HttpStatus.OK.value())
                    .data(emptyPage(page, size))
                    .error(null)
                    .build();
            return new ResponseEntity<>(empty, HttpStatus.OK);
        }

        Integer roleFilter = roleId == null || roleId.isBlank() ? null : Integer.valueOf(roleId);
        String scopeFilter = scopeType == null || scopeType.isBlank() ? null : scopeType;
        List<Long> filteredUserIds = null;
        if (roleFilter != null || scopeFilter != null) {
            filteredUserIds = authService.findUserIds(rawToken(authorization), roleFilter, scopeFilter);
        }

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.userService.selectByStoreIdPage(allowedStoreIds, page, size, search, filteredUserIds))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    private List<Integer> parseStoreIds(String storeIds) {
        if (storeIds == null || storeIds.isBlank()) {
            return List.of();
        }
        List<Integer> parsed = new ArrayList<>();
        for (String value : storeIds.split(",")) {
            String trimmed = value.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                parsed.add(Integer.valueOf(trimmed));
            } catch (NumberFormatException ignored) {
                // Ignore malformed ids rather than failing the whole page.
            }
        }
        return parsed;
    }

    private String rawToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new IllegalStateException("Missing bearer token");
        }
        return authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
    }

    /**
     * Deletes a User by its ID.
     *
     * @param id the user ID
     * @param username the username
     * @return ResponseEntity with deletion status
     * @throws Exception if an error occurs during deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('employee:edit', 'user:delete', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> deleteUser(@PathVariable Integer id,
                                                      @RequestHeader("X-Username") String username,
                                                      @RequestHeader(value = "Authorization", required = false) String authorization) throws Exception {
        requireUserStore(id);
        int rowCount = this.userService.delete(authorization, id);
        log.debug("user deleted {} ", rowCount);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Updates a User by its ID.
     *
     * @param id the user ID
     * @param username the username
     * @param userEditDto the user edit data transfer object
     * @return ResponseEntity with update status
     * @throws Exception if an error occurs during update
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('employee:edit', 'user:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> updateUser(@PathVariable Integer id,
                    @RequestHeader("X-Username") String username,
                    @RequestHeader(value = "Authorization", required = false) String authorization,
                    @Valid @RequestBody UserEditDto userEditDto) throws Exception {
        requireUserStore(id);
        int rowCount = this.userService.update(authorization, userEditDto);
        log.debug("user updated {} ", rowCount);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves a User by username.
     *
     * @param username the username of the user
     * @return ResponseEntity containing the user data
     * @throws Exception if an error occurs during retrieval
     */
    @GetMapping("/{username}")
    @PreAuthorize("hasAnyAuthority('menu:view', 'catalog:read', 'organization:read', 'order:view', 'terminal:view', 'availability:view', 'employee:view', 'user:read', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> detailUser(@PathVariable String username) throws Exception {
        User user = this.userService.selectByUsername(username);
        CallerScopeContext.current().requireStore(user == null ? null : user.getStoreId());
        log.debug("user detail {} ", user);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(user)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    private void requireUserStore(Integer userId) {
        User user = userService.selectById(userId);
        CallerScopeContext.current().requireStore(user == null ? null : user.getStoreId());
    }

    private Map<String, Object> emptyPage(int page, int size) {
        Map<String, Object> empty = new HashMap<>();
        empty.put("page", 0);
        empty.put("size", size);
        empty.put("total", 0);
        empty.put("data", List.of());
        empty.put("navigate", List.of());
        return empty;
    }
}
