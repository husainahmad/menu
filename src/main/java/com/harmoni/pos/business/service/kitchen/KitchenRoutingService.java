package com.harmoni.pos.business.service.kitchen;

import com.harmoni.pos.menu.model.KitchenCategoryRouting;
import com.harmoni.pos.menu.model.KitchenProductRouting;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenCategoryRoutingDto;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenProductRoutingDto;

import java.util.List;
import java.util.Map;

/**
 * Category and product to station routing.
 */
public interface KitchenRoutingService {

    /**
     * Returns both routing lists for the store.
     *
     * @param storeId the store
     * @return a map with {@code categories} and {@code products}
     */
    Map<String, Object> listByStore(Integer storeId);

    KitchenCategoryRouting routeCategory(Integer storeId, KitchenCategoryRoutingDto dto);

    KitchenProductRouting routeProduct(Integer storeId, KitchenProductRoutingDto dto);

    void deleteCategoryRouting(Integer storeId, Integer categoryId);

    void deleteProductRouting(Integer storeId, Integer productId);

    List<KitchenCategoryRouting> categoryRoutings(Integer storeId);

    List<KitchenProductRouting> productRoutings(Integer storeId);

    /**
     * Loads the store's routing tables once, so an order with many lines does not
     * query per line.
     *
     * @param storeId the store
     * @return the routing tables
     */
    RoutingTable loadRoutingTable(Integer storeId);

    /**
     * Immutable routing lookup. Product routing wins over category routing.
     *
     * @param productStations  product id to station id
     * @param categoryStations category id to station id
     */
    record RoutingTable(Map<Integer, Integer> productStations,
                        Map<Integer, Integer> categoryStations) {

        /**
         * Resolves the station for an order line.
         *
         * @param productId  the product
         * @param categoryId the product's category
         * @return the station id, or {@code null} when the line is unassigned
         */
        public Integer resolve(Integer productId, Integer categoryId) {
            Integer productStation = productId == null ? null : productStations.get(productId);
            if (productStation != null) {
                return productStation;
            }
            return categoryId == null ? null : categoryStations.get(categoryId);
        }
    }
}
