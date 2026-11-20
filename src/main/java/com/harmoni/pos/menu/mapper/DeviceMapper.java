package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Mapper for {@code devices}.
 */
@Mapper
public interface DeviceMapper {

    int insert(Device row);

    int updateByPrimaryKey(Device row);

    int deleteByPrimaryKey(@Param("id") Integer id, @Param("deletedAt") Date deletedAt);

    Device selectByPrimaryKey(@Param("id") Integer id);

    List<Device> selectByStoreId(@Param("storeId") Integer storeId);

    Device selectByStoreIdAndName(@Param("storeId") Integer storeId,
                                  @Param("name") String name);

    Device selectByStoreIdAndAddress(@Param("storeId") Integer storeId,
                                     @Param("address") String address);

    int countByStoreId(@Param("storeId") Integer storeId);
}
