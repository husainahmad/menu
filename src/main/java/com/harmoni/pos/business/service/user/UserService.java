package com.harmoni.pos.business.service.user;

import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.User;
import com.harmoni.pos.menu.model.dto.UserDto;
import com.harmoni.pos.menu.model.dto.edit.UserEditDto;

import java.util.List;
import java.util.Map;

public interface UserService {
    int insert(String token, UserDto userDto);
    int update(String token, UserEditDto userEditDto);
    User selectById(Integer id);
    int delete(String token, Integer id);
    User selectByUsername(String username);
    User selectByUsernameAuthIdAndStoreId(String name, Integer authId, Integer storeId);
    List<User> selectByStoreIds(List<Store> stores, String search);
    Map<String, Object> selectByChainId(Integer chainId, int page, int size, String search);
}
