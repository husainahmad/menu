package com.harmoni.pos.business.service.store;

import com.harmoni.pos.business.service.store.tier.StoreTierService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.menu.mapper.StoreMapper;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.StoreTier;
import com.harmoni.pos.menu.model.dto.StoreDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    private StoreMapper storeMapper;

    @Mock
    private StoreTierService storeTierService;

    @InjectMocks
    private StoreServiceImpl storeService;

    private StoreDto storeDto;
    private Store store;

    @BeforeEach
    void setUp() {
        storeDto = new StoreDto();
        storeDto.setName("Store 1");
        storeDto.setChainId(1);
        storeDto.setAddress("123 Main St");
        storeDto.setTierMenuId(10);
        storeDto.setTierPriceId(20);
        storeDto.setTierServiceId(30);

        store = new Store().setId(1).setName("Store 1").setChainId(1);
    }

    @Test
    void create_shouldSucceed() {
        when(storeMapper.selectByNameChainId("Store 1", 1)).thenReturn(null);
        when(storeMapper.insert(any(Store.class))).thenReturn(1);
        when(storeTierService.insert(any(StoreTier.class))).thenReturn(1);

        int result = storeService.create(storeDto);
        assertEquals(1, result);
    }

    @Test
    void create_shouldThrow_whenDuplicate() {
        when(storeMapper.selectByNameChainId("Store 1", 1)).thenReturn(store);
        assertThrows(BusinessBadRequestException.class, () -> storeService.create(storeDto));
    }

    @Test
    void create_shouldThrow_whenInsertFails() {
        when(storeMapper.selectByNameChainId("Store 1", 1)).thenReturn(null);
        when(storeMapper.insert(any(Store.class))).thenReturn(0);
        assertThrows(BusinessNoContentRequestException.class, () -> storeService.create(storeDto));
    }

    @Test
    void delete_shouldSucceed() {
        when(storeMapper.selectByPrimaryKey(1)).thenReturn(store);
        when(storeMapper.deleteByPrimaryKey(1)).thenReturn(1);

        assertEquals(1, storeService.delete(1));
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(storeMapper.selectByPrimaryKey(1)).thenReturn(null);
        assertThrows(BusinessNotFoundRequestException.class, () -> storeService.delete(1));
    }

    @Test
    void get_shouldReturnStore() {
        when(storeMapper.selectByPrimaryKey(1)).thenReturn(store);
        assertNotNull(storeService.get(1));
    }

    @Test
    void get_shouldThrow_whenNotFound() {
        when(storeMapper.selectByPrimaryKey(1)).thenReturn(null);
        assertThrows(BusinessNotFoundRequestException.class, () -> storeService.get(1));
    }

    @Test
    void getAllStoresByChainId_shouldReturnList() {
        when(storeMapper.selectAllByChainId(1, "")).thenReturn(List.of(store));
        assertFalse(storeService.getAllStoresByChainId(1, "").isEmpty());
    }

    @Test
    void update_shouldSucceed() {
        when(storeMapper.selectByPrimaryKey(1)).thenReturn(store);
        when(storeMapper.updateByPrimaryKey(any(Store.class))).thenReturn(1);
        when(storeTierService.insertOrUpdateByStoreId(any(StoreTier.class))).thenReturn(1);

        assertEquals(1, storeService.update(1, storeDto));
    }
}
