package com.harmoni.pos.business.service.tier;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.menu.mapper.TierMapper;
import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.TierType;
import com.harmoni.pos.menu.model.dto.TierDto;
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
class TierServiceImplTest {

    @Mock
    private TierMapper tierMapper;

    @InjectMocks
    private TierServiceImpl tierService;

    private TierDto tierDto;
    private Tier tier;

    @BeforeEach
    void setUp() {
        tierDto = new TierDto();
        tierDto.setName("Premium");
        tierDto.setBrandId(1);
        tierDto.setType(TierType.PRICE);

        tier = new Tier().setId(1).setName("Premium").setBrandId(1).setType(TierType.PRICE);
    }

    @Test
    void create_shouldSucceed() {
        when(tierMapper.selectByNameAndBrandId("Premium", 1)).thenReturn(null);
        when(tierMapper.insert(any(Tier.class))).thenReturn(1);

        int result = tierService.create(tierDto);
        assertEquals(1, result);
    }

    @Test
    void create_shouldThrow_whenDuplicate() {
        when(tierMapper.selectByNameAndBrandId("Premium", 1)).thenReturn(tier);
        assertThrows(BusinessBadRequestException.class, () -> tierService.create(tierDto));
    }

    @Test
    void create_shouldThrow_whenInsertFails() {
        when(tierMapper.selectByNameAndBrandId("Premium", 1)).thenReturn(null);
        when(tierMapper.insert(any(Tier.class))).thenReturn(0);
        assertThrows(BusinessNoContentRequestException.class, () -> tierService.create(tierDto));
    }

    @Test
    void update_shouldSucceed() {
        when(tierMapper.selectByPrimaryKey(1)).thenReturn(tier);
        when(tierMapper.updateByPrimaryKey(any(Tier.class))).thenReturn(1);

        assertTrue(tierService.update(tierDto, 1));
    }

    @Test
    void update_shouldThrow_whenUpdateFails() {
        when(tierMapper.selectByPrimaryKey(1)).thenReturn(tier);
        when(tierMapper.updateByPrimaryKey(any(Tier.class))).thenReturn(0);

        assertThrows(BusinessNoContentRequestException.class, () -> tierService.update(tierDto, 1));
    }

    @Test
    void delete_shouldSucceed() {
        when(tierMapper.selectByPrimaryKey(1)).thenReturn(tier);
        when(tierMapper.deleteByPrimaryKey(1)).thenReturn(1);

        assertEquals(1, tierService.delete(1));
    }

    @Test
    void get_shouldReturnTier() {
        when(tierMapper.selectByPrimaryKey(1)).thenReturn(tier);
        assertNotNull(tierService.get(1));
    }

    @Test
    void get_shouldThrow_whenNotFound() {
        when(tierMapper.selectByPrimaryKey(1)).thenReturn(null);
        assertThrows(BusinessNotFoundRequestException.class, () -> tierService.get(1));
    }

    @Test
    void getByBrandId_shouldReturnList() {
        when(tierMapper.selectByBrandId(1)).thenReturn(List.of(tier));
        assertFalse(tierService.getByBrandId(1).isEmpty());
    }

    @Test
    void getByBrandIdAndTierType_shouldReturnList() {
        when(tierMapper.selectByBrandIdTierType(1, TierType.PRICE)).thenReturn(List.of(tier));
        assertFalse(tierService.getByBrandIdAndTierType(1, TierType.PRICE).isEmpty());
    }

    @Test
    void validateTierByIds_shouldPass() {
        when(tierMapper.selectByIds(List.of(1))).thenReturn(List.of(tier));
        assertDoesNotThrow(() -> tierService.validateTierByIds(List.of(1)));
    }
}
