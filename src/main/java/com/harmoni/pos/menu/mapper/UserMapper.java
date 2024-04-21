package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(User row);
    User selectByPrimaryKey(Integer id);
    User selectByEmail(String email);
    User selectByEmailPassword(String email, String password);
    int updateByPrimaryKey(User row);

}