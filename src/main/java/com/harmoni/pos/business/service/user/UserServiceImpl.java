package com.harmoni.pos.business.service.user;

import com.github.pagehelper.PageInfo;
import com.harmoni.pos.business.service.auth.AuthService;
import com.harmoni.pos.business.service.brand.BrandService;
import com.harmoni.pos.business.service.chain.ChainService;
import com.harmoni.pos.business.service.store.StoreService;
import com.harmoni.pos.component.JwtUtil;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.http.utils.PaginationUtils;
import com.harmoni.pos.menu.mapper.UserMapper;
import com.harmoni.pos.menu.model.Brand;
import com.harmoni.pos.menu.model.Chain;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.User;
import com.harmoni.pos.menu.model.dto.AssignmentDto;
import com.harmoni.pos.menu.model.dto.UserDto;
import com.harmoni.pos.menu.model.dto.edit.UserEditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of {@link UserService} for managing User entities.
 * Provides business logic for creating, updating, deleting, and retrieving users.
 */
@RequiredArgsConstructor
@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final StoreService storeService;
    private final ChainService chainService;
    private final BrandService brandService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    private static final String AUTH_FAILED_EXCEPTION = "exception.auth.process.Failed";
    private static final String USER_NOT_FOUND_EXCEPTION = "exception.user.id.NotFound";
    private static final String USER_AUTH_ID_EXCEPTION = "exception.user.authId.NotFound";

    /**
     * Inserts a new user.
     *
     * @param token the authentication token of the requester
     * @param userDto the data transfer object containing user details
     * @return the ID of the inserted user
     * @throws BusinessNotFoundRequestException if authentication fails or user already exists
     */
    @Override
    public int insert(String authorization, UserDto userDto) {
        if (ObjectUtils.isNotEmpty(this.selectByUsernameAuthIdAndStoreId(userDto.getUsername(),
                userDto.getAuthId(), userDto.getStoreId()))) {
            throw new BusinessNotFoundRequestException(USER_NOT_FOUND_EXCEPTION, null);
        }

        String status = userDto.getStatus() == null ? "ACTIVE" : userDto.getStatus();
        userDto.setStatus(status);
        userDto.setEnabled("ACTIVE".equalsIgnoreCase(status));
        if (userDto.getEmployeeCode() == null || userDto.getEmployeeCode().isBlank()) {
            userDto.setEmployeeCode(generateEmployeeCode(firstRoleCode(userDto.getAssignments())));
        }

        Integer authUserId = authService.create(rawToken(authorization), userDto);
        if (authUserId == null || authUserId == 0) {
            throw new BusinessNotFoundRequestException(AUTH_FAILED_EXCEPTION, null);
        }

        User user = userDto.toUser();
        user.setAuthUserId(authUserId.longValue());
        if (user.getAuthId() == null) {
            user.setAuthId(firstRoleId(userDto.getRoleIds()));
        }
        return userMapper.insert(user);
    }

    /**
     * Updates an existing user.
     *
     * @param token the authentication token of the requester
     * @param userEditDto the data transfer object containing updated user details
     * @return the number of records updated
     * @throws BusinessNotFoundRequestException if authentication fails
     */
    @Override
    public int update(String authorization, UserEditDto userEditDto) {
        User user = selectById(userEditDto.getId());
        user.setUsername(userEditDto.getUsername());
        user.setStoreId(userEditDto.getStoreId());
        user.setAuthId(userEditDto.getAuthId());
        if (user.getAuthId() == null) {
            user.setAuthId(firstRoleId(userEditDto.getRoleIds()));
        }
        if (userEditDto.getEmployeeCode() != null && !userEditDto.getEmployeeCode().isBlank()) {
            user.setEmployeeCode(userEditDto.getEmployeeCode());
        }
        if (userEditDto.getStatus() != null) {
            user.setStatus(userEditDto.getStatus());
        }
        userEditDto.setEnabled(!"INACTIVE".equalsIgnoreCase(user.getStatus()));
        if (authService.update(rawToken(authorization), userEditDto)==0) {
            throw new BusinessNotFoundRequestException(AUTH_FAILED_EXCEPTION, null);
        }
        return userMapper.updateByPrimaryKey(user);
    }

    private String rawToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new IllegalStateException("Missing bearer token");
        }
        return authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
    }

    private String generateEmployeeCode(String roleCode) {
        String prefix = roleCode == null || roleCode.isBlank() ? "EMP" : roleCode;
        int next = userMapper.countByEmployeeCodePrefix(prefix + "-") + 1;
        return prefix + "-" + String.format("%03d", next);
    }

    private String firstRoleCode(List<AssignmentDto> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            return null;
        }
        return assignments.stream()
                .map(AssignmentDto::getRoleCode)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private Integer firstRoleId(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return null;
        }
        return roleIds.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(Long::intValue)
                .orElse(null);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the User object
     * @throws BusinessNotFoundRequestException if the user is not found
     */
    @Override
    public User selectById(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(user)) {
            throw new BusinessNotFoundRequestException(USER_NOT_FOUND_EXCEPTION, null);
        }
        return userMapper.selectByPrimaryKey(id);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param token the authentication token of the requester
     * @param id the ID of the user to delete
     * @return the number of records deleted
     * @throws BusinessNotFoundRequestException if the user is not found or authentication fails
     */
    @Override
    public int delete(String authorization, Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(user)) {
            throw new BusinessNotFoundRequestException(USER_NOT_FOUND_EXCEPTION, null);
        }
        if (authService.delete(rawToken(authorization), user.getUsername())==0) {
            throw new BusinessNotFoundRequestException(AUTH_FAILED_EXCEPTION, null);
        }
        return userMapper.deleteByPrimaryKey(user.getId());
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return the User object
     * @throws BusinessNotFoundRequestException if the user is not found
     */
    @Override
    public User selectByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            throw new BusinessNotFoundRequestException(USER_AUTH_ID_EXCEPTION, null);
        }
        Store store = storeService.get(user.getStoreId());
        user.setStore(store);
        Chain chain = chainService.get(store.getChainId());
        store.setChain(chain);
        Brand brand = brandService.get(chain.getBrandId());
        chain.setBrand(brand);
        return user;
    }

    /**
     * Retrieves a user by their authentication token.
     *
     * @param authToken the authentication token of the user
     * @return the User object
     */
    @Override
    public User selectByAuthToken(String authToken) {
        return this.selectByUsername(jwtUtil.extractUsername(authToken));
    }

    /**
     * Retrieves a user by username, authentication ID, and store ID.
     *
     * @param name the username of the user
     * @param authId the authentication ID
     * @param storeId the store ID
     * @return the User object, or null if not found
     */
    @Override
    public User selectByUsernameAuthIdAndStoreId(String name, Integer authId, Integer storeId) {
        return userMapper.selectByUsernameAuthIdAndStoreId(name, authId, storeId);
    }

    /**
     * Retrieves users by a list of stores and an optional search term.
     *
     * @param stores the list of stores to filter users
     * @param search the search term to filter users
     * @return a list of User objects matching the criteria
     */
    @Override
    public List<User> selectByStoreIds(List<Store> stores, String search, List<Long> filteredUserIds) {
        return userMapper.selectByListChain(stores, search, filteredUserIds);
    }

    /**
     * Retrieves users by chain ID with pagination and an optional search term.
     *
     * @param chainId the chain ID to filter users
     * @param page the page number for pagination
     * @param size the number of records per page
     * @param search the search term to filter users
     * @return a map containing user data and pagination information
     */
    @Override
    public Map<String, Object> selectByChainId(Integer chainId, int page, int size, String search) {
        List<Store> stores = this.storeService.getAllStoresByChainId(chainId, "");
        if (stores == null || stores.isEmpty()) {
            return emptyPage(size);
        }
        PaginationUtils.applyPagination(page, size);

        Map<String, Object> paginationData = new HashMap<>();
        PageInfo<User> productPageInfo = new PageInfo<>(selectByStoreIds(stores, search, null));

        paginationData.put("page", productPageInfo.getPages());
        paginationData.put("size", productPageInfo.getSize());
        paginationData.put("total", productPageInfo.getTotal());
        paginationData.put("data", productPageInfo.getList());
        paginationData.put("navigate", productPageInfo.getNavigatepageNums());

        return paginationData;
    }

    @Override
    public Map<String, Object> selectByStoreIdPage(List<Integer> storeIds, int page, int size, String search,
                                                   List<Long> filteredUserIds) {
        if (storeIds != null && storeIds.isEmpty()) {
            return emptyPage(size);
        }
        // A null list means "no store filter": used for global operators, who
        // may see every user. An empty list means "no accessible store" and is
        // handled above so it cannot widen access.
        List<Store> stores = null;
        if (storeIds != null) {
            stores = new ArrayList<>();
            for (Integer storeId : storeIds) {
                if (storeId != null) {
                    Store store = new Store();
                    store.setId(storeId);
                    stores.add(store);
                }
            }
        }
        PaginationUtils.applyPagination(page, size);

        PageInfo<User> pageInfo = new PageInfo<>(selectByStoreIds(stores, search, filteredUserIds));
        Map<String, Object> paginationData = new HashMap<>();
        paginationData.put("page", pageInfo.getPages());
        paginationData.put("size", pageInfo.getSize());
        paginationData.put("total", pageInfo.getTotal());
        paginationData.put("data", pageInfo.getList());
        paginationData.put("navigate", pageInfo.getNavigatepageNums());
        return paginationData;
    }

    private Map<String, Object> emptyPage(int size) {
        Map<String, Object> empty = new HashMap<>();
        empty.put("page", 0);
        empty.put("size", size);
        empty.put("total", 0L);
        empty.put("data", List.of());
        empty.put("navigate", List.of());
        return empty;
    }

}
