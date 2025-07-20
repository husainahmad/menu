package com.harmoni.pos.business.service.user;

import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.User;
import com.harmoni.pos.menu.model.dto.UserDto;
import com.harmoni.pos.menu.model.dto.edit.UserEditDto;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing User entities.
 */
public interface UserService {

    /**
     * Inserts a new user.
     *
     * @param token the authentication token of the requester
     * @param userDto the data transfer object containing user details
     * @return the ID of the inserted user
     */
    int insert(String token, UserDto userDto);

    /**
     * Updates an existing user.
     *
     * @param token the authentication token of the requester
     * @param userEditDto the data transfer object containing updated user details
     * @return the number of records updated
     */
    int update(String token, UserEditDto userEditDto);

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the User object, or null if not found
     */
    User selectById(Integer id);

    /**
     * Deletes a user by their ID.
     *
     * @param token the authentication token of the requester
     * @param id the ID of the user to delete
     * @return the number of records deleted
     */
    int delete(String token, Integer id);

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return the User object, or null if not found
     */
    User selectByUsername(String username);

    /**
     * Retrieves a user by their authentication token.
     *
     * @param authToken the authentication token of the user
     * @return the User object, or null if not found
     */
    User selectByAuthToken(String authToken);

    /**
     * Retrieves a user by username, authentication ID, and store ID.
     *
     * @param name the username of the user
     * @param authId the authentication ID
     * @param storeId the store ID
     * @return the User object, or null if not found
     */
    User selectByUsernameAuthIdAndStoreId(String name, Integer authId, Integer storeId);

    /**
     * Retrieves users by a list of stores and an optional search term.
     *
     * @param stores the list of stores to filter users
     * @param search the search term to filter users
     * @return a list of User objects matching the criteria
     */
    List<User> selectByStoreIds(List<Store> stores, String search);

    /**
     * Retrieves users by chain ID with pagination and an optional search term.
     *
     * @param chainId the chain ID to filter users
     * @param page the page number for pagination
     * @param size the number of records per page
     * @param search the search term to filter users
     * @return a map containing user data and pagination information
     */
    Map<String, Object> selectByChainId(Integer chainId, int page, int size, String search);
}
