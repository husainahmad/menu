package com.harmoni.pos.business.service.store.tier;

import com.harmoni.pos.menu.mapper.StoreTierMapper;
import com.harmoni.pos.menu.model.StoreTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreTierServiceImplTest {

    @Mock
    private StoreTierMapper storeTierMapper;

    @InjectMocks
    private StoreTierServiceImpl storeTierService;

    private StoreTier storeTier;

    @BeforeEach
    void setUp() {
        storeTier = new StoreTier()
                .setStoreId(1)
                .setTierMenuId(10)
                .setTierPriceId(20)
                .setTierServiceId(30);
    }

    @Test
    void insert_shouldReturnRows() {
        when(storeTierMapper.insert(storeTier)).thenReturn(1);
        assertEquals(1, storeTierService.insert(storeTier));
    }

    @Test
    void selectByPrimaryKey_shouldReturnStoreTier() {
        when(storeTierMapper.selectByPrimaryKey(1)).thenReturn(storeTier);
        assertNotNull(storeTierService.selectByPrimaryKey(1));
    }

    @Test
    void selectByStoreId_shouldReturnStoreTier() {
        when(storeTierMapper.selectByStoreId(1)).thenReturn(storeTier);
        assertNotNull(storeTierService.selectByStoreId(1));
    }

    @Test
    void insertOrUpdateByStoreId_shouldReturnRows() {
        when(storeTierMapper.insertOrUpdateByStoreId(storeTier)).thenReturn(1);
        assertEquals(1, storeTierService.insertOrUpdateByStoreId(storeTier));
    }
}
