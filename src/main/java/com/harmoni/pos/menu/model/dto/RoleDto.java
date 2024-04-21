package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Role;
import lombok.Data;

@Data
public class RoleDto {
    private String name;

    public Role toRole() {
        return new Role()
                .setName(name);
    }
}
