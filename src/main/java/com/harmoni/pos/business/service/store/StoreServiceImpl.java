package com.harmoni.pos.business.service.store;

import com.github.pagehelper.PageInfo;
import com.harmoni.pos.business.service.store.tier.StoreTierService;
import com.harmoni.pos.component.CallerScope;
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

/**
 * Implementation of {@link StoreService} for managing {@link Store} entities and related
 * operations such as creation, retrieval, update, deletion, and pagination.
 */
@RequiredArgsConstructor
@Service("storeService")
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final StoreMapper storeMapper;
    private final StoreTierService storeTierService;

    /**
     * Creates a new store record along with its store tier.
     *
     * @param storeDto the data transfer object containing store and store tier data
     * @return the ID of the newly created store
     * @throws BusinessBadRequestException if a store with the same name already exists in the chain
     * @throws BusinessNoContentRequestException if the insert operation fails
     */
    @Override
    public int create(StoreDto storeDto) {
        if (!ObjectUtils.isEmpty(storeMapper.selectByNameChainId(storeDto.getName(), storeDto.getChainId()))) {
            throw new BusinessBadRequestException("exception.store.badRequest.duplicate",
                    PosObjectUtils.appendValue(new ArrayList<>().toArray(), storeDto.getName()));
        }

        Store store = storeDto.toStore();
        store.setCreatedAt(new Date(System.currentTimeMillis()));

        int inserted = storeMapper.insert(store);
        if (inserted < 1) {
            throw new BusinessNoContentRequestException(BusinessNoContentRequestException.NO_CONTENT, null);
        }

        StoreTier storeTier = storeDto.toStoreTier();
        storeTier.setStoreId(inserted);

        storeTierService.insert(storeTier);

        return inserted;
    }

    /**
     * Deletes a store by its ID.
     *
     * @param id the ID of the store to delete
     * @return the number of rows affected by the delete operation
     * @throws BusinessNotFoundRequestException if the store with the given ID does not exist
     */
    @Override
    public int delete(Integer id) {
        Store store = this.get(id);
        return storeMapper.deleteByPrimaryKey(store.getId());
    }

    /**
     * Retrieves a store by its ID.
     *
     * @param id the store ID
     * @return the {@link Store} entity
     * @throws BusinessNotFoundRequestException if the store is not found
     */
    @Override
    public Store get(Integer id) {
        Store store = storeMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(store)) {
            throw new BusinessNotFoundRequestException("exception.store.id.notFound", null);
        }
        return store;
    }

    /**
     * Retrieves stores filtered by chain ID, caller scope and optional search keyword with pagination.
     *
     * <p>The scope is applied in the query so the pagination metadata reflects only the
     * stores the caller may see.</p>
     *
     * @param scope the caller scope used to restrict the stores, may be {@code null} for unrestricted access
     * @param chainId the chain ID to filter stores
     * @param page the page number (1-based)
     * @param size the page size
     * @param search optional search keyword to filter by store name or attributes
     * @return a map containing pagination metadata and list of stores under keys: "page", "size", "total", "data", "navigate"
     */
    @Override
    public Map<String, Object> getAllStoresByChainIdPaginated(CallerScope scope, Integer chainId, int page, int size, String search) {
        PaginationUtils.applyPagination(page, size);

        List<Integer> storeIds = scope == null || scope.isGlobal() ? null : new ArrayList<>(scope.getStoreIds());
        PageInfo<Store> storePageInfo = new PageInfo<>(getAllStoresByChainId(chainId, search, storeIds));

        Map<String, Object> paginationData = new HashMap<>();
        paginationData.put("page", storePageInfo.getPages());
        paginationData.put("size", storePageInfo.getSize());
        paginationData.put("total", storePageInfo.getTotal());
        paginationData.put("data", storePageInfo.getList());
        paginationData.put("navigate", storePageInfo.getNavigatepageNums());

        return paginationData;
    }

    /**
     * Retrieves all stores for a given chain ID with an optional search keyword without pagination.
     *
     * @param chainId the chain ID
     * @param search optional search keyword
     * @return a list of matching {@link Store} entities
     */
    @Override
    public List<Store> getAllStoresByChainId(Integer chainId, String search) {
        return getAllStoresByChainId(chainId, search, null);
    }

    private List<Store> getAllStoresByChainId(Integer chainId, String search, List<Integer> storeIds) {
        return storeMapper.selectAllByChainId(chainId, search, storeIds);
    }

    /**
     * Updates a store record and its associated store tier by store ID.
     *
     * @param id the store ID to update
     * @param storeDto the data transfer object containing updated store and store tier data
     * @return the number of rows affected by the store tier update operation
     * @throws BusinessNotFoundRequestException if the store to update does not exist
     */
    @Override
    public int update(Integer id, StoreDto storeDto) {
        Store store = this.get(id);
        Store storeUpdated = storeDto.toStore();
        storeUpdated.setId(store.getId());
        storeUpdated.setCreatedAt(store.getCreatedAt());
        storeUpdated.setUpdatedAt(new Date(System.currentTimeMillis()));
        storeUpdated.setTimeZone(store.getTimeZone());
        if (storeUpdated.getOrderTypes() == null) {
            storeUpdated.setOrderTypes(store.getOrderTypes());
        }
        if (storeUpdated.getTaxServiceMode() == null) {
            storeUpdated.setTaxServiceMode(store.getTaxServiceMode());
        }
        if (storeUpdated.getTaxPercent() == null) {
            storeUpdated.setTaxPercent(store.getTaxPercent());
        }
        if (storeUpdated.getServiceChargePercent() == null) {
            storeUpdated.setServiceChargePercent(store.getServiceChargePercent());
        }
        if (storeUpdated.getReceiptLogoMode() == null) {
            storeUpdated.setReceiptLogoMode(store.getReceiptLogoMode());
        }
        if (storeUpdated.getLogoUrl() == null) {
            storeUpdated.setLogoUrl(store.getLogoUrl());
        }
        if (storeUpdated.getMenuPriceMode() == null) {
            storeUpdated.setMenuPriceMode(store.getMenuPriceMode());
        }
        if (storeUpdated.getOrderTypeMode() == null) {
            storeUpdated.setOrderTypeMode(store.getOrderTypeMode());
        }

        storeMapper.updateByPrimaryKey(storeUpdated);
        StoreTier storeTier = storeDto.toStoreTier();
        storeTier.setStoreId(storeUpdated.getId());

        return storeTierService.insertOrUpdateByStoreId(storeTier);
    }
}
