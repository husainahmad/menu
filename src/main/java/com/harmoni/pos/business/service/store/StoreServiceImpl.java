package com.harmoni.pos.business.service.store;

import com.github.pagehelper.PageInfo;
import com.harmoni.pos.business.service.store.tier.StoreTierService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.http.utils.PaginationUtils;
import com.harmoni.pos.http.utils.PosObjectUtils;
import com.harmoni.pos.menu.mapper.StoreMapper;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.StoreTier;
import com.harmoni.pos.menu.model.dto.StoreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.util.*;

@RequiredArgsConstructor
@Service("storeService")
@Slf4j
public class StoreServiceImpl implements StoreService {
    private final StoreMapper storeMapper;
    private final StoreTierService storeTierService;

    @Override
    public int create(StoreDto storeDto) {

        if (!ObjectUtils.isEmpty(storeMapper.selectByNameChainId(storeDto.getName(), storeDto.getChainId()))) {
            throw new BusinessBadRequestException("exception.store.badRequest.duplicate",
                    PosObjectUtils.appendValue(new ArrayList<>().toArray(), storeDto.getName()));
        }

        Store store = storeDto.toStore();
        store.setCreatedAt(new Date(System.currentTimeMillis()));

        int inserted = storeMapper.insert(store);
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
    public int delete(Integer id) {
        Store store = this.get(id);
        return storeMapper.deleteByPrimaryKey(store.getId());
    }

    @Override
    public Store get(Integer id) {
        Store store = storeMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(store)) {
            throw new BusinessNotFoundRequestException("exception.store.id.notFound", null);
        }
        return store;
    }

    @Override
    public Map<String, Object> getAllStoresByChainIdPaginated(Integer chainId, int page, int size, String search) {

        PaginationUtils.applyPagination(page, size);

        Map<String, Object> paginationData = new HashMap<>();
        PageInfo<Store> productPageInfo = new PageInfo<>(getAllStoresByChainId(chainId, search));

        paginationData.put("page", productPageInfo.getPages());
        paginationData.put("size", productPageInfo.getSize());
        paginationData.put("total", productPageInfo.getTotal());
        paginationData.put("data", productPageInfo.getList());
        paginationData.put("navigate", productPageInfo.getNavigatepageNums());

        return paginationData;
    }

    @Override
    public List<Store> getAllStoresByChainId(Integer chainId, String search) {
        return storeMapper.selectAllByChainId(chainId, search);
    }

    @Override
    public int update(Integer id, StoreDto storeDto) {
        Store store = this.get(id);
        Store storeUpdated = storeDto.toStore();
        storeUpdated.setId(store.getId());
        storeUpdated.setUpdatedAt(new Date(System.currentTimeMillis()));

        storeMapper.updateByPrimaryKey(storeUpdated);
        StoreTier storeTier = storeDto.toStoreTier();
        storeTier.setStoreId(storeUpdated.getId());

        return storeTierService.insertOrUpdateByStoreId(storeTier);
    }
}
