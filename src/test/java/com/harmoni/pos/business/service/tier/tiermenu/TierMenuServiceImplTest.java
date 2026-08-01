package com.harmoni.pos.business.service.tier.tiermenu;

import com.harmoni.pos.business.service.tier.TierService;
import com.harmoni.pos.menu.mapper.TierMenuMapper;
import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.TierMenu;
import com.harmoni.pos.menu.model.dto.add.TierMenuEditDto;
import com.harmoni.pos.menu.model.dto.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TierMenuServiceImplTest {

    @Mock
    private TierService tierService;

    @Mock
    private TierMenuMapper tierMenuMapper;

    @InjectMocks
    private TierMenuServiceImpl tierMenuService;

    private Tier tier;
    private TierMenuEditDto editDto;

    @BeforeEach
    void setUp() {
        tier = new Tier().setId(1).setName("Gold").setBrandId(1);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Drinks");
        categoryDto.setDescription("Beverages");
        categoryDto.setBrandId(1);

        editDto = new TierMenuEditDto();
        editDto.setCategoryDto(new com.harmoni.pos.menu.model.dto.edit.CategoryEditDto());
        editDto.getCategoryDto().setId(10);
        editDto.setActive(true);
    }

    @Test
    void create_shouldSucceed() {
        when(tierService.get(1)).thenReturn(tier);
        when(tierMenuMapper.updateTierMenuBulk(anyList())).thenReturn(2);

        int result = tierMenuService.create(1, List.of(editDto));
        assertEquals(2, result);
    }

    @Test
    void getMenusByBrandId_shouldReturnList() {
        when(tierMenuMapper.selectByBrandId(1)).thenReturn(List.of(new TierMenu()));
        assertFalse(tierMenuService.getMenusByBrandId(1).isEmpty());
    }

    @Test
    void getMenusByTierId_shouldReturnList() {
        when(tierMenuMapper.selectByTierId(1)).thenReturn(List.of(new TierMenu()));
        assertFalse(tierMenuService.getMenusByTierId(1).isEmpty());
    }
}
