package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.KitchenCategoryRouting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for {@code kitchen_category_routings}.
 */
@Mapper
public interface KitchenCategoryRoutingMapper {

    int insertOrUpdate(KitchenCategoryRouting row);

    int deleteByStoreIdAndCategoryId(@Param("storeId") Integer storeId,
                                     @Param("categoryId") Integer categoryId);

    KitchenCategoryRouting selectByStoreIdAndCategoryId(@Param("storeId") Integer storeId,
                                                        @Param("categoryId") Integer categoryId);

    List<KitchenCategoryRouting> selectByStoreId(@Param("storeId") Integer storeId);

    /**
     * Lightweight read used while routing an order.
     *
     * @param storeId the store
     * @return category routes with station information
     */
    List<KitchenCategoryRouting> selectRoutingByStoreId(@Param("storeId") Integer storeId);
}
