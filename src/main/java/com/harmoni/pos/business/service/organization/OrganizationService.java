package com.harmoni.pos.business.service.organization;

import com.harmoni.pos.menu.model.dto.OrganizationTreeDto;

/**
 * Builds the nested brand → chain → store view used by the organization page.
 */
public interface OrganizationService {

    /**
     * Returns every brand with its chains and stores, including the counts
     * shown in the tree.
     *
     * @return the organization tree
     */
    OrganizationTreeDto tree();
}
