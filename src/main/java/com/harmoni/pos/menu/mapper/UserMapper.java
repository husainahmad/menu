package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper interface for User entity database operations.
 */
@Mapper
public interface UserMapper {

    /**
     * Deletes a User by its primary key.
     * @param id the User ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Inserts a new User.
     * @param row the User object
     * @return number of rows affected
     */
    int insert(User row);

    /**
     * Selects a User by its primary key.
     * @param id the User ID
     * @return the User object
     */
    User selectByPrimaryKey(Integer id);

    /**
     * Selects a User by username.
     * @param username the username
     * @return the User object
     */
    User selectByUsername(String username);

    /**
     * Selects a User by username, authentication ID, and store ID.
     * @param username the username
     * @param authId the authentication ID
     * @param storeId the store ID
     * @return the User object
     */
    User selectByUsernameAuthIdAndStoreId(String username, Integer authId, Integer storeId);

    /**
     * Selects Users by a list of stores and search term.
     * @param stores the list of Store objects
     * @param search the search term
     * @return list of User objects
     */
    List<User> selectByListChain(List<Store> stores, String search);

    /**
     * Updates a User by its primary key.
     * @param row the User object
     * @return number of rows affected
     */
    int updateByPrimaryKey(User row);

}