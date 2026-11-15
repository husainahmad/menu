package com.harmoni.pos.business.service.kitchen;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.KitchenPrinterMapper;
import com.harmoni.pos.menu.mapper.KitchenStationMapper;
import com.harmoni.pos.menu.mapper.StoreMapper;
import com.harmoni.pos.menu.model.KitchenStation;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenStationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KitchenStationServiceImplTest {

    @Mock
    private KitchenStationMapper kitchenStationMapper;
    @Mock
    private KitchenPrinterMapper kitchenPrinterMapper;
    @Mock
    private StoreMapper storeMapper;

    @InjectMocks
    private KitchenStationServiceImpl service;

    private KitchenStationDto dto;

    @BeforeEach
    void setUp() {
        dto = new KitchenStationDto();
        dto.setName("Grill");
        dto.setCode("GRL");
    }

    @Test
    void createScopesStationToStore() {
        when(storeMapper.selectByPrimaryKey(2)).thenReturn(new Store().setId(2).setName("Main"));
        when(kitchenStationMapper.insert(any())).thenReturn(1);

        KitchenStation created = service.create(2, dto);

        assertEquals(2, created.getStoreId());
        assertEquals("GRL", created.getCode());
        assertEquals("ACTIVE", created.getStatus());
    }

    @Test
    void duplicateCodeInStoreIsRejected() {
        when(storeMapper.selectByPrimaryKey(2)).thenReturn(new Store().setId(2));
        when(kitchenStationMapper.selectByStoreIdAndCode(2, "GRL"))
                .thenReturn(new KitchenStation().setId(99));

        assertThrows(BusinessBadRequestException.class, () -> service.create(2, dto));
        verify(kitchenStationMapper, never()).insert(any());
    }

    @Test
    void missingStoreIsRejected() {
        when(storeMapper.selectByPrimaryKey(2)).thenReturn(null);

        assertThrows(BusinessBadRequestException.class, () -> service.create(2, dto));
    }
}
