package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.KitchenProductRouting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for {@code kitchen_product_routings}.
 */
@Mapper
public interface KitchenProductRoutingMapper {

    int insertOrUpdate(KitchenProductRouting row);

    int deleteByStoreIdAndProductId(@Param("storeId") Integer storeId,
                                    @Param("productId") Integer productId);

    KitchenProductRouting selectByStoreIdAndProductId(@Param("storeId") Integer storeId,
                                                      @Param("productId") Integer productId);

    List<KitchenProductRouting> selectByStoreId(@Param("storeId") Integer storeId);

    /**
     * Lightweight read used while routing an order.
     *
     * @param storeId the store
     * @return product routes with station information
     */
    List<KitchenProductRouting> selectRoutingByStoreId(@Param("storeId") Integer storeId);
}
