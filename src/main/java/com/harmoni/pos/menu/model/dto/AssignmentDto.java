package com.harmoni.pos.menu.model.dto;

import lombok.Data;

/**
 * A scoped role assignment sent to the authentication service.
 */
@Data
public class AssignmentDto {

    private Long roleId;
    private String roleCode;
    private String scopeType;
    private Long scopeId;
}
