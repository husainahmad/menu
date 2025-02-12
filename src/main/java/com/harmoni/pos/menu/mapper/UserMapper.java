package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(User row);
    User selectByPrimaryKey(Integer id);
    User selectByUsername(String username);
    User selectByUsernameAuthIdAndStoreId(String username, Integer authId, Integer storeId);
    List<User> selectByListChain(List<Store> stores, String search);
    int updateByPrimaryKey(User row);

}