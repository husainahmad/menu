package com.harmoni.pos.business.service.tier.tierservice;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.TierMapper;
import com.harmoni.pos.menu.mapper.TierServiceMapper;
import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.TierService;
import com.harmoni.pos.menu.model.dto.SubServiceDto;
import com.harmoni.pos.menu.model.dto.TierServiceDto;
import com.harmoni.pos.menu.model.dto.edit.TierEditDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TierServiceServiceImplTest {

    @Mock
    private TierServiceMapper tierServiceMapper;

    @Mock
    private TierMapper tierMapper;

    @InjectMocks
    private TierServiceServiceImpl tierServiceService;

    private TierServiceDto tierServiceDto;

    @BeforeEach
    void setUp() {
        TierEditDto tierEditDto = new TierEditDto();
        tierEditDto.setId(1);
        tierEditDto.setName("Gold");
        tierEditDto.setBrandId(1);

        SubServiceDto subServiceDto = new SubServiceDto();
        subServiceDto.setId(5);
        subServiceDto.setName("Sub");
        subServiceDto.setServiceId(10);

        tierServiceDto = new TierServiceDto();
        tierServiceDto.setTierDto(tierEditDto);
        tierServiceDto.setSubServiceDto(subServiceDto);
        tierServiceDto.setActive(true);
    }

    @Test
    void create_shouldSucceed() {
        when(tierMapper.selectByNameAndBrandId("Gold", 1)).thenReturn(null);
        when(tierMapper.insert(any(Tier.class))).thenReturn(1);

        int result = tierServiceService.create(tierServiceDto);
        assertEquals(1, result);
    }

    @Test
    void create_shouldThrow_whenDuplicate() {
        when(tierMapper.selectByNameAndBrandId("Gold", 1)).thenReturn(new Tier());
        assertThrows(BusinessBadRequestException.class, () -> tierServiceService.create(tierServiceDto));
    }

    @Test
    void create_shouldThrow_whenInsertFails() {
        when(tierMapper.selectByNameAndBrandId("Gold", 1)).thenReturn(null);
        when(tierMapper.insert(any(Tier.class))).thenReturn(0);
        assertThrows(BusinessNoContentRequestException.class, () -> tierServiceService.create(tierServiceDto));
    }

    @Test
    void updateTierServices_shouldSucceed() {
        when(tierServiceMapper.updateTierServicesBulk(anyList())).thenReturn(2);
        assertEquals(2, tierServiceService.updateTierServices(List.of(tierServiceDto), 1));
    }

    @Test
    void getByBrandId_shouldReturnList() {
        when(tierServiceMapper.selectByBrandId(1)).thenReturn(List.of(new TierService()));
        assertFalse(tierServiceService.getByBrandId(1).isEmpty());
    }
}
