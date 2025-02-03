package com.harmoni.pos.business.service.store;

import com.harmoni.pos.business.service.store.tier.StoreTierService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.http.utils.PosObjectUtils;
import com.harmoni.pos.menu.mapper.StoreMapper;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.StoreTier;
import com.harmoni.pos.menu.model.dto.StoreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service("storeService")
@Slf4j
public class StoreServiceImpl implements StoreService {
    private final StoreMapper storeMapper;
    private final StoreTierService storeTierService;

    @Override
    public int create(StoreDto storeDto) {

        if (!ObjectUtils.isEmpty(storeMapper.selectByNameTierIdChainId(storeDto.getName(), storeDto.getChainId()))) {
            throw new BusinessBadRequestException("exception.store.badRequest.duplicate",
                    PosObjectUtils.appendValue(new ArrayList<>().toArray(), storeDto.getName()));
        }

        int inserted = storeMapper.insert(storeDto.toStore());
        if (inserted<1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        StoreTier storeTier = storeDto.toStoreTier();
        storeTier.setStoreId(inserted);

        storeTierService.insert(storeTier);

        return inserted;
    }

    @Override
    public int delete(Long id) {
        Store store = this.get(id);
        return storeMapper.deleteByPrimaryKey(store.getId());
    }

    @Override
    public Store get(Long id) {
        Store store = storeMapper.selectByPrimaryKey(id.intValue());
        if (ObjectUtils.isEmpty(store)) {
            throw new BusinessNotFoundRequestException("exception.store.id.notFound", null);
        }
        return store;
    }

    @Override
    public List<Store> getAllStoresByBrandId(Long chainId) {
        return storeMapper.selectAllByBrandId(chainId.intValue());
    }

    @Override
    public int update(Long id, StoreDto storeDto) {
        if (ObjectUtils.isEmpty(storeMapper.selectByPrimaryKey(id.intValue()))) {
            throw new BusinessNotFoundRequestException("exception.store.id.notFound", null);
        }

        Store storeUpdated = storeDto.toStore();
        storeUpdated.setUpdatedAt(new Date(System.currentTimeMillis()));
        return storeMapper.updateByPrimaryKey(storeUpdated);
    }
}
