package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Customization;
import java.util.List;

/**
 * Mapper interface for performing CRUD operations on the {@code customizations} table.
 */

public interface CustomizationMapper {

    /**
     * Inserts a new customization record.
     *
     * @param customization the customization to insert
     * @return number of rows affected
     */
    int insert(Customization customization);

    /**
     * Updates an existing customization by primary key.
     *
     * @param customization the customization to update
     * @return number of rows affected
     */
    int updateByPrimaryKey(Customization customization);

    /**
     * Deletes a customization by its primary key.
     *
     * @param id the ID of the customization to delete
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Retrieves a customization by its primary key.
     *
     * @param id the ID of the customization
     * @return the found customization or null if not found
     */
    Customization selectByPrimaryKey(Integer id);

    /**
     * Retrieves all customizations.
     *
     * @return list of all customizations
     */
    List<Customization> selectAll();

    /**
     * Retrieves all customizations for a given brand ID.
     *
     * @param brandId the ID of the brand
     * @return list of customizations belonging to the brand
     */
    List<Customization> selectByBrandId(Integer brandId);
}
