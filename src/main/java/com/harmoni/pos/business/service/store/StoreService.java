package com.harmoni.pos.business.service.store;

import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.dto.StoreDto;

import java.util.List;
import java.util.Map;

public interface StoreService {

    int create(StoreDto storeDto);
    int delete(Integer id);
    Store get(Integer id);
    Map<String, Object> getAllStoresByChainIdPaginated(Integer chainId, int page, int size, String search);
    List<Store> getAllStoresByChainId(Integer chainId, String search);
    int update(Integer id, StoreDto storeDto);
}
