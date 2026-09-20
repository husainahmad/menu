package com.harmoni.pos.business.service.sku;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.CustomizationOptionMapper;
import com.harmoni.pos.menu.mapper.ProductCustomizationMapper;
import com.harmoni.pos.menu.mapper.SkuCustomizationOptionMapper;
import com.harmoni.pos.menu.mapper.SkuMapper;
import com.harmoni.pos.menu.model.CustomizationOption;
import com.harmoni.pos.menu.model.ProductCustomization;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.SkuCustomizationOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkuCustomizationOptionServiceImplTest {

    @Mock
    private SkuCustomizationOptionMapper mapper;

    @Mock
    private SkuMapper skuMapper;

    @Mock
    private CustomizationOptionMapper customizationOptionMapper;

    @Mock
    private ProductCustomizationMapper productCustomizationMapper;

    @InjectMocks
    private SkuCustomizationOptionServiceImpl skuCustomizationOptionService;

    private SkuCustomizationOption option;
    private Sku sku;
    private CustomizationOption customizationOption;

    @BeforeEach
    void setUp() {
        option = new SkuCustomizationOption().setId(1).setSkuId(10).setCustomizationOptionId(20);
        sku = new Sku().setId(10).setProductId(5);
        customizationOption = new CustomizationOption();
        customizationOption.setId(20);
        customizationOption.setCustomizationId(30);
    }

    @Test
    void getById_shouldReturn() {
        when(mapper.selectByPrimaryKey(1)).thenReturn(option);
        assertTrue(skuCustomizationOptionService.getById(1).isPresent());
    }

    @Test
    void getById_shouldReturnEmpty() {
        when(mapper.selectByPrimaryKey(1)).thenReturn(null);
        assertTrue(skuCustomizationOptionService.getById(1).isEmpty());
    }

    @Test
    void getBySkuId_shouldReturnList() {
        when(mapper.selectBySkuId(10)).thenReturn(List.of(option));
        assertFalse(skuCustomizationOptionService.getBySkuId(10).isEmpty());
    }

    @Test
    void create_shouldReturnRows_whenOptionBelongsToProduct() {
        when(skuMapper.selectById(10)).thenReturn(sku);
        when(customizationOptionMapper.selectByPrimaryKey(20)).thenReturn(customizationOption);
        when(productCustomizationMapper.selectByProductId(5))
                .thenReturn(List.of(new ProductCustomization().setCustomizationId(30)));
        when(mapper.insert(option)).thenReturn(1);

        assertEquals(1, skuCustomizationOptionService.create(option));
    }

    @Test
    void create_shouldThrow_whenOptionNotInProduct() {
        when(skuMapper.selectById(10)).thenReturn(sku);
        when(customizationOptionMapper.selectByPrimaryKey(20)).thenReturn(customizationOption);
        when(productCustomizationMapper.selectByProductId(5))
                .thenReturn(List.of(new ProductCustomization().setCustomizationId(99)));

        assertThrows(BusinessBadRequestException.class, () -> skuCustomizationOptionService.create(option));
    }

    @Test
    void update_shouldReturnRows_whenOptionBelongsToProduct() {
        when(skuMapper.selectById(10)).thenReturn(sku);
        when(customizationOptionMapper.selectByPrimaryKey(20)).thenReturn(customizationOption);
        when(productCustomizationMapper.selectByProductId(5))
                .thenReturn(List.of(new ProductCustomization().setCustomizationId(30)));
        when(mapper.updateByPrimaryKey(option)).thenReturn(1);

        assertEquals(1, skuCustomizationOptionService.update(option));
    }

    @Test
    void update_shouldThrow_whenOptionNotInProduct() {
        when(skuMapper.selectById(10)).thenReturn(sku);
        when(customizationOptionMapper.selectByPrimaryKey(20)).thenReturn(customizationOption);
        when(productCustomizationMapper.selectByProductId(5))
                .thenReturn(List.of(new ProductCustomization().setCustomizationId(99)));

        assertThrows(BusinessBadRequestException.class, () -> skuCustomizationOptionService.update(option));
    }

    @Test
    void delete_shouldReturnRows() {
        when(mapper.deleteByPrimaryKey(1)).thenReturn(1);
        assertEquals(1, skuCustomizationOptionService.delete(1));
    }
}
