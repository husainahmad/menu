package com.harmoni.pos.business.service.store;

import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.dto.StoreDto;

import java.util.List;
import java.util.Map;

public interface StoreService {

    int create(StoreDto storeDto);
    int delete(Long id);
    Store get(Long id);
    Map<String, Object> getAllStoresByChainIdPaginated(Long chainId, int page, int size, String search);
    List<Store> getAllStoresByChainId(Long chainId, String search);
    int update(Long id, StoreDto storeDto);
}
