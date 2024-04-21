package com.harmoni.pos.business.service.role;

import com.harmoni.pos.menu.mapper.RoleMapper;
import com.harmoni.pos.menu.model.dto.RoleDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("roleService")
public class RoleServiceImpl implements RoleService {

    private final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final RoleMapper roleMapper;

    @Override
    public int create(RoleDto roleDto) {
        return 0;
    }
}
