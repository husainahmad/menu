package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Service;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper interface for Service entity database operations.
 */
@Mapper
public interface ServiceMapper {

    /**
     * Deletes a Service by its primary key.
     * @param id the Service ID
     * @return number of rows affected
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Inserts a new Service.
     * @param row the Service object
     * @return number of rows affected
     */
    int insert(Service row);

    /**
     * Selects a Service by its primary key.
     * @param id the Service ID
     * @return the Service object
     */
    Service selectByPrimaryKey(Integer id);

    /**
     * Selects a Service by its name.
     * @param name the Service name
     * @return the Service object
     */
    Service selectByName(String name);

    /**
     * Updates a Service by its primary key.
     * @param row the Service object
     * @return number of rows affected
     */
    int updateByPrimaryKey(Service row);

    /**
     * Selects all Services and their sub-services.
     * @return list of Service objects
     */
    List<Service> selectAllAndSubService();

}