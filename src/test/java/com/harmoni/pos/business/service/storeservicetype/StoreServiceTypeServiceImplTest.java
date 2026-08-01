package com.harmoni.pos.business.service.storeservicetype;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.StoreServiceTypeMapper;
import com.harmoni.pos.menu.model.StoreServiceType;
import com.harmoni.pos.menu.model.dto.StoreServiceTypeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTypeServiceImplTest {

    @Mock
    private StoreServiceTypeMapper storeServiceTypeMapper;

    @InjectMocks
    private StoreServiceTypeServiceImpl storeServiceTypeService;

    private StoreServiceTypeDto dto;

    @BeforeEach
    void setUp() {
        dto = new StoreServiceTypeDto();
        dto.setStoreId(1);
        dto.setSubServiceId(2);
    }

    @Test
    void create_shouldSucceed() {
        when(storeServiceTypeMapper.selectByStoreIdSubServiceId(1, 2)).thenReturn(null);
        when(storeServiceTypeMapper.insert(any(StoreServiceType.class))).thenReturn(1);

        int result = storeServiceTypeService.create(dto);
        assertEquals(1, result);
    }

    @Test
    void create_shouldThrow_whenDuplicate() {
        when(storeServiceTypeMapper.selectByStoreIdSubServiceId(1, 2)).thenReturn(new StoreServiceType());

        assertThrows(BusinessBadRequestException.class, () -> storeServiceTypeService.create(dto));
    }

    @Test
    void create_shouldThrow_whenInsertFails() {
        when(storeServiceTypeMapper.selectByStoreIdSubServiceId(1, 2)).thenReturn(null);
        when(storeServiceTypeMapper.insert(any(StoreServiceType.class))).thenReturn(0);

        assertThrows(BusinessNoContentRequestException.class, () -> storeServiceTypeService.create(dto));
    }
}
