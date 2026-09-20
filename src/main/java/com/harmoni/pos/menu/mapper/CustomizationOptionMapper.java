package com.harmoni.pos.menu.mapper;
import com.harmoni.pos.menu.model.CustomizationOption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MyBatis mapper for {@link CustomizationOption}.
 */
@Mapper
public interface CustomizationOptionMapper {

    /**
     * Inserts a new customization option.
     *
     * @param option the option to insert
     * @return number of rows affected (1 if successful, otherwise 0)
     */
    int insert(CustomizationOption option);

    /**
     * Inserts or updates a list of options belonging to a customization.
     * Existing options are matched by their unique key and updated in place.
     *
     * @param customizationOptions the list of options to insert or update
     * @param customizationId      the parent customization ID
     * @return number of rows affected
     */
    int insertOrUpdateBulk(List<CustomizationOption> customizationOptions, Integer customizationId);

    /**
     * Updates an existing customization option by primary key.
     *
     * @param option the option with updated data
     * @return number of rows affected
     */
    int updateByPrimaryKey(CustomizationOption option);

    /**
     * Deletes a customization option by its primary key.
     *
     * @param id the option ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Retrieves a customization option by its primary key.
     *
     * @param id the option ID
     * @return the found option, or null if not found
     */
    CustomizationOption selectByPrimaryKey(Integer id);

    /**
     * Retrieves all non-deleted options for a single customization.
     *
     * @param customizationId the parent customization ID
     * @return list of options for the customization
     */
    List<CustomizationOption> selectByCustomizationId(Integer customizationId);

    /**
     * Retrieves all non-deleted options for a list of customizations.
     * Used for batch-loading options across multiple customizations.
     *
     * @param customizationIds list of customization IDs to query
     * @return list of options belonging to the given customizations
     */
    List<CustomizationOption> selectByCustomizationIds(List<Integer> customizationIds);

}