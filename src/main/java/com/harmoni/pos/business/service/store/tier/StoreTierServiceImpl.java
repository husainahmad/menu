package com.harmoni.pos.business.service.store.tier;

import com.harmoni.pos.menu.mapper.StoreTierMapper;
import com.harmoni.pos.menu.model.StoreTier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("storeTierService")
public class StoreTierServiceImpl implements StoreTierService {

    private final StoreTierMapper storeTierMapper;

    @Override
    public int insert(StoreTier row) {
        return storeTierMapper.insert(row);
    }

    @Override
    public StoreTier selectByPrimaryKey(Integer id) {
        return storeTierMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insertOrUpdateByStoreId(StoreTier row) {
        return storeTierMapper.insertOrUpdateByStoreId(row);
    }
}
