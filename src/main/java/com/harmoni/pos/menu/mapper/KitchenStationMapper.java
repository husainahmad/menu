package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.KitchenStation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Mapper for {@code kitchen_stations}.
 */
@Mapper
public interface KitchenStationMapper {

    int insert(KitchenStation row);

    int updateByPrimaryKey(KitchenStation row);

    int deleteByPrimaryKey(@Param("id") Integer id, @Param("deletedAt") Date deletedAt);

    KitchenStation selectByPrimaryKey(@Param("id") Integer id);

    List<KitchenStation> selectByStoreId(@Param("storeId") Integer storeId);

    KitchenStation selectByStoreIdAndCode(@Param("storeId") Integer storeId,
                                          @Param("code") String code);

    KitchenStation selectByIdAndStore(@Param("id") Integer id,
                                      @Param("storeId") Integer storeId);
}
