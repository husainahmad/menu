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
import com.harmoni.pos.menu.model.dto.UserDto;
import com.harmoni.pos.menu.model.dto.edit.UserEditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public int insert(String username, UserDto userDto) {
        if (authService.create(username, userDto)==0) {
            throw new BusinessNotFoundRequestException(AUTH_FAILED_EXCEPTION, null);
        }
        if ((ObjectUtils.isNotEmpty(this.selectByUsernameAuthIdAndStoreId(userDto.getUsername(), userDto.getAuthId(),
                userDto.getStoreId())))) {
            throw new BusinessNotFoundRequestException(USER_NOT_FOUND_EXCEPTION, null);
        }

        return userMapper.insert(userDto.toUser());
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
    public int update(String username, UserEditDto userEditDto) {
        User user = selectById(userEditDto.getId());
        user.setUsername(userEditDto.getUsername());
        user.setStoreId(userEditDto.getStoreId());
        user.setAuthId(userEditDto.getAuthId());
        if (authService.update(username, userEditDto)==0) {
            throw new BusinessNotFoundRequestException(AUTH_FAILED_EXCEPTION, null);
        }
        return userMapper.updateByPrimaryKey(user);
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
    public int delete(String username, Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(user)) {
            throw new BusinessNotFoundRequestException(USER_NOT_FOUND_EXCEPTION, null);
        }
        if (authService.delete(username, user.getUsername())==0) {
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
    public List<User> selectByStoreIds(List<Store> stores, String search) {
        return userMapper.selectByListChain(stores, search);
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
        PaginationUtils.applyPagination(page, size);

        List<Store> stores = this.storeService.getAllStoresByChainId(chainId, "");
        Map<String, Object> paginationData = new HashMap<>();
        PageInfo<User> productPageInfo = new PageInfo<>(selectByStoreIds(stores, search));

        paginationData.put("page", productPageInfo.getPages());
        paginationData.put("size", productPageInfo.getSize());
        paginationData.put("total", productPageInfo.getTotal());
        paginationData.put("data", productPageInfo.getList());
        paginationData.put("navigate", productPageInfo.getNavigatepageNums());

        return paginationData;
    }

}
