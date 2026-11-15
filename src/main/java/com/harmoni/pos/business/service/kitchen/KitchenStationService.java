package com.harmoni.pos.business.service.kitchen;

import com.harmoni.pos.menu.model.KitchenStation;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenStationDto;

import java.util.List;

/**
 * CRUD and lookup operations for kitchen stations.
 */
public interface KitchenStationService {

    KitchenStation create(Integer storeId, KitchenStationDto dto);

    KitchenStation update(Integer id, KitchenStationDto dto);

    void delete(Integer id);

    KitchenStation get(Integer id);

    KitchenStation get(Integer storeId, Integer id);

    List<KitchenStation> listByStore(Integer storeId);
}
