package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper {

    int deleteByPrimaryKey(Integer id);
    int insert(Role row);
    Role selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(Role row);

}