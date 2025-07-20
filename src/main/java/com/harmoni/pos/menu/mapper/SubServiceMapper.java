package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.SubService;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper interface for SubService entity database operations.
 */
@Mapper
public interface SubServiceMapper {

    /**
     * Deletes a SubService by its primary key.
     * @param id the SubService ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Inserts a new SubService.
     * @param row the SubService object
     * @return number of rows affected
     */
    int insert(SubService row);

    /**
     * Selects a SubService by its primary key.
     * @param id the SubService ID
     * @return the SubService object
     */
    SubService selectByPrimaryKey(Integer id);

    /**
     * Selects a SubService by its name and Service ID.
     * @param name the SubService name
     * @param serviceId the Service ID
     * @return the SubService object
     */
    SubService selectByNameServiceId(String name, Integer serviceId);

    /**
     * Updates a SubService by its primary key.
     * @param row the SubService object
     * @return number of rows affected
     */
    int updateByPrimaryKey(SubService row);

}