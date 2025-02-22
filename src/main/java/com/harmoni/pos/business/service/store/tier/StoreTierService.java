package com.harmoni.pos.business.service.store.tier;

import com.harmoni.pos.menu.model.StoreTier;

public interface StoreTierService {
    int insert(StoreTier row);
    StoreTier selectByPrimaryKey(Integer id);
    StoreTier selectByStoreId(Integer storeId);
    int insertOrUpdateByStoreId(StoreTier row);
}
