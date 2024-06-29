package com.harmoni.pos.business.service.role;

import com.harmoni.pos.menu.mapper.RoleMapper;
import com.harmoni.pos.menu.model.dto.RoleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("roleService")
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public int create(RoleDto roleDto) {
        return 0;
    }
}
