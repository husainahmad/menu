package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Service;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /**
     * Counts every Service row.
     * @return the total number of services
     */
    int countAll();

    /**
     * Selects one page of Service ids ordered by id.
     * @param offset the number of rows to skip
     * @param size   the page size
     * @return the ids on the page
     */
    List<Integer> selectPagedIds(@Param("offset") int offset, @Param("size") int size);

    /**
     * Selects the given Services together with their sub-services.
     * @param ids the service ids to load
     * @return list of Service objects
     */
    List<Service> selectByIdsAndSubService(@Param("ids") List<Integer> ids);

}