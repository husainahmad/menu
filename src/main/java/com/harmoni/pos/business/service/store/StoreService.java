package com.harmoni.pos.business.service.store;

import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.dto.StoreDto;
import com.harmoni.pos.menu.model.dto.TierDto;

import java.util.List;

public interface StoreService {

    int create(StoreDto storeDto);
    int delete(Long id);
    Store get(Long id);
    List<Store> list();
    int update(Long id, StoreDto storeDto);
}
