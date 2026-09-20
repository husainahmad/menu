package com.harmoni.pos.business.service.product;

import com.harmoni.pos.business.service.category.CategoryService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.CustomizationMapper;
import com.harmoni.pos.menu.mapper.CustomizationOptionMapper;
import com.harmoni.pos.menu.mapper.CustomizationOptionTierPriceMapper;
import com.harmoni.pos.menu.mapper.ProductCustomizationMapper;
import com.harmoni.pos.menu.mapper.ProductMapper;
import com.harmoni.pos.menu.model.*;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationConfigDto;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCustomizationServiceImplTest {

    @Mock
    private ProductCustomizationMapper mapper;
    @Mock
    private CustomizationMapper customizationMapper;
    @Mock
    private CustomizationOptionMapper customizationOptionMapper;
    @Mock
    private CustomizationOptionTierPriceMapper customizationOptionTierPriceMapper;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductCustomizationServiceImpl productCustomizationService;

    private ProductCustomization pc;
    private Customization customization;

    @BeforeEach
    void setUp() {
        pc = new ProductCustomization().setId(1).setProductId(10).setCustomizationId(20);
        customization = new Customization()
                .setId(20)
                .setName("Topping")
                .setDescription("Choose your topping")
                .setSelectionType(SelectionType.MULTIPLE)
                .setBrandId(1)
                .setRequired(true)
                .setMinimumSelection(1)
                .setMaximumSelection(3);
    }

    @Test
    void getById_shouldReturn() {
        when(mapper.selectByPrimaryKey(1)).thenReturn(pc);
        assertTrue(productCustomizationService.getById(1).isPresent());
    }

    @Test
    void getById_shouldReturnEmpty() {
        when(mapper.selectByPrimaryKey(1)).thenReturn(null);
        assertTrue(productCustomizationService.getById(1).isEmpty());
    }

    @Test
    void getByProductId_shouldReturnList() {
        when(mapper.selectByProductId(10)).thenReturn(List.of(pc));
        assertFalse(productCustomizationService.getByProductId(10).isEmpty());
    }

    @Test
    void create_shouldReturnRows() {
        when(mapper.insert(pc)).thenReturn(1);
        assertEquals(1, productCustomizationService.create(pc));
    }

    @Test
    void update_shouldReturnRows() {
        when(mapper.updateByPrimaryKey(pc)).thenReturn(1);
        assertEquals(1, productCustomizationService.update(pc));
    }

    @Test
    void delete_shouldReturnRows() {
        when(mapper.deleteByPrimaryKey(1)).thenReturn(1);
        assertEquals(1, productCustomizationService.delete(1));
    }

    @Test
    void replaceByProductId_shouldDeleteThenInsertOrdered() {
        when(mapper.insertBulk(anyList())).thenAnswer(invocation -> {
            List<ProductCustomization> links = invocation.getArgument(0);
            assertEquals(0, links.get(0).getSortOrder());
            assertEquals(1, links.get(1).getSortOrder());
            return 2;
        });

        int rows = productCustomizationService.replaceByProductId(List.of(20, 30, 20), 10);

        assertEquals(2, rows);
        verify(mapper).deleteByProductId(10);
        verify(mapper).insertBulk(anyList());
    }

    @Test
    void replaceByProductId_emptyClearsAllLinks() {
        assertEquals(0, productCustomizationService.replaceByProductId(null, 10));
        verify(mapper).deleteByProductId(10);
        verify(mapper, never()).insertBulk(anyList());
    }

    @Test
    void replaceForProduct_shouldValidateAndReplace() {
        Product product = new Product().setId(10).setCategoryId(5);
        when(productMapper.selectByPrimaryKey(10)).thenReturn(product);
        when(customizationMapper.selectByIds(List.of(20, 30))).thenReturn(
                List.of(customization, new Customization().setId(30).setBrandId(1)));
        when(categoryService.get(5)).thenReturn(new Category().setId(5).setBrandId(1));
        when(mapper.insertBulk(anyList())).thenReturn(2);

        assertEquals(2, productCustomizationService.replaceForProduct(10, List.of(20, 30)));
        verify(mapper).deleteByProductId(10);
    }

    @Test
    void replaceForProduct_shouldThrowWhenProductNotFound() {
        when(productMapper.selectByPrimaryKey(10)).thenReturn(null);
        assertThrows(BusinessBadRequestException.class,
                () -> productCustomizationService.replaceForProduct(10, List.of(20)));
    }

    @Test
    void replaceForProduct_shouldThrowWhenCustomizationNotFound() {
        Product product = new Product().setId(10).setCategoryId(5);
        when(productMapper.selectByPrimaryKey(10)).thenReturn(product);
        when(customizationMapper.selectByIds(List.of(99))).thenReturn(List.of());

        assertThrows(BusinessBadRequestException.class,
                () -> productCustomizationService.replaceForProduct(10, List.of(99)));
    }

    @Test
    void replaceForProduct_shouldThrowOnBrandMismatch() {
        Product product = new Product().setId(10).setCategoryId(5);
        when(productMapper.selectByPrimaryKey(10)).thenReturn(product);
        when(customizationMapper.selectByIds(List.of(20))).thenReturn(List.of(customization));
        when(categoryService.get(5)).thenReturn(new Category().setId(5).setBrandId(99));

        assertThrows(BusinessBadRequestException.class,
                () -> productCustomizationService.replaceForProduct(10, List.of(20)));
    }

    @Test
    void getDetailedByProductId_shouldResolveEffectiveValuesAndOptions() {
        pc.setSortOrder(0);
        pc.setRequiredOverride(false);
        pc.setMinSelectionOverride(2);
        pc.setMaxSelectionOverride(4);

        CustomizationOption option1 = new CustomizationOption()
                .setId(101).setCustomizationId(20).setName("Extra Cheese")
                .setTierPrices(List.of(new CustomizationOptionTierPrice()
                        .setCustomizationOptionId(101).setTierId(1).setPrice(new BigDecimal("1.50"))));
        option1.setId(101);
        customization.setCustomizationOptions(List.of(option1));

        when(mapper.selectByProductId(10)).thenReturn(List.of(pc));
        when(customizationMapper.selectByIds(List.of(20))).thenReturn(List.of(customization));
        when(customizationOptionMapper.selectByCustomizationIds(List.of(20))).thenReturn(List.of(option1));
        when(customizationOptionTierPriceMapper.selectByOptionIds(List.of(101))).thenReturn(option1.getTierPrices());

        List<ProductCustomizationResponseDto> result = productCustomizationService.getDetailedByProductId(10);

        assertEquals(1, result.size());
        ProductCustomizationResponseDto dto = result.get(0);
        assertEquals(1, dto.getId());
        assertEquals("Topping", dto.getName());
        assertEquals(false, dto.getRequired());
        assertEquals(2, dto.getMinSelection());
        assertEquals(4, dto.getMaxSelection());
        assertEquals(0, dto.getSortOrder());
        assertEquals(1, dto.getOptions().size());
        assertEquals(1, dto.getOptions().get(0).getTierPrices().size());
    }

    @Test
    void getDetailedByProductId_shouldReturnEmptyWhenNoLinks() {
        when(mapper.selectByProductId(10)).thenReturn(List.of());
        assertTrue(productCustomizationService.getDetailedByProductId(10).isEmpty());
    }

    @Test
    void updateConfiguration_shouldApplyOverrides() {
        pc.setProductId(10);
        pc.setCustomizationId(20);
        when(mapper.selectByPrimaryKey(1)).thenReturn(pc);
        when(customizationMapper.selectByPrimaryKey(20)).thenReturn(customization);
        when(customizationOptionMapper.selectByCustomizationId(20)).thenReturn(
                List.of(new CustomizationOption().setId(101),
                        new CustomizationOption().setId(102),
                        new CustomizationOption().setId(103)));

        when(mapper.updateConfigurationById(any())).thenAnswer(invocation -> {
            ProductCustomization link = invocation.getArgument(0);
            assertEquals(false, link.getRequiredOverride());
            assertEquals(1, link.getMinSelectionOverride());
            assertEquals(3, link.getMaxSelectionOverride());
            assertEquals(2, link.getSortOrder());
            return 1;
        });

        ProductCustomizationConfigDto config = new ProductCustomizationConfigDto();
        config.setRequiredOverride(false);
        config.setMinSelectionOverride(1);
        config.setMaxSelectionOverride(3);
        config.setSortOrder(2);

        assertEquals(1, productCustomizationService.updateConfiguration(1, config));
    }

    @Test
    void updateConfiguration_shouldThrowWhenLinkNotFound() {
        when(mapper.selectByPrimaryKey(99)).thenReturn(null);
        assertThrows(BusinessBadRequestException.class,
                () -> productCustomizationService.updateConfiguration(99, new ProductCustomizationConfigDto()));
    }

    @Test
    void updateConfiguration_shouldThrowWhenMaxBelowMin() {
        when(mapper.selectByPrimaryKey(1)).thenReturn(pc);
        when(customizationMapper.selectByPrimaryKey(20)).thenReturn(customization);

        ProductCustomizationConfigDto config = new ProductCustomizationConfigDto();
        config.setMinSelectionOverride(4);
        config.setMaxSelectionOverride(2);

        assertThrows(BusinessBadRequestException.class,
                () -> productCustomizationService.updateConfiguration(1, config));
    }

    @Test
    void updateConfiguration_singleSelectionOnlyAllowsMaxOne() {
        customization.setSelectionType(SelectionType.SINGLE);
        when(mapper.selectByPrimaryKey(1)).thenReturn(pc);
        when(customizationMapper.selectByPrimaryKey(20)).thenReturn(customization);

        ProductCustomizationConfigDto config = new ProductCustomizationConfigDto();
        config.setMaxSelectionOverride(2);

        assertThrows(BusinessBadRequestException.class,
                () -> productCustomizationService.updateConfiguration(1, config));
    }

    @Test
    void updateConfiguration_requiredMustAllowAtLeastOne() {
        customization.setRequired(true);
        customization.setMinimumSelection(0);
        when(mapper.selectByPrimaryKey(1)).thenReturn(pc);
        when(customizationMapper.selectByPrimaryKey(20)).thenReturn(customization);

        ProductCustomizationConfigDto config = new ProductCustomizationConfigDto();
        config.setRequiredOverride(true);
        config.setMinSelectionOverride(0);

        assertThrows(BusinessBadRequestException.class,
                () -> productCustomizationService.updateConfiguration(1, config));
    }

    @Test
    void updateConfiguration_maxExceedsAvailableOptions() {
        when(mapper.selectByPrimaryKey(1)).thenReturn(pc);
        when(customizationMapper.selectByPrimaryKey(20)).thenReturn(customization);
        when(customizationOptionMapper.selectByCustomizationId(20)).thenReturn(
                List.of(new CustomizationOption().setId(101)));

        ProductCustomizationConfigDto config = new ProductCustomizationConfigDto();
        config.setMaxSelectionOverride(5);

        assertThrows(BusinessBadRequestException.class,
                () -> productCustomizationService.updateConfiguration(1, config));
    }

    @Test
    void updateConfiguration_nullOverridesKeepMasterValues() {
        pc.setProductId(10);
        pc.setCustomizationId(20);
        customization.setRequired(true);
        customization.setMinimumSelection(1);
        customization.setMaximumSelection(3);
        when(mapper.selectByPrimaryKey(1)).thenReturn(pc);
        when(customizationMapper.selectByPrimaryKey(20)).thenReturn(customization);
        when(customizationOptionMapper.selectByCustomizationId(20)).thenReturn(
                List.of(new CustomizationOption().setId(101),
                        new CustomizationOption().setId(102),
                        new CustomizationOption().setId(103)));

        when(mapper.updateConfigurationById(any())).thenAnswer(invocation -> {
            ProductCustomization link = invocation.getArgument(0);
            assertNull(link.getRequiredOverride());
            assertNull(link.getMinSelectionOverride());
            assertNull(link.getMaxSelectionOverride());
            return 1;
        });

        ProductCustomizationConfigDto config = new ProductCustomizationConfigDto();

        assertEquals(1, productCustomizationService.updateConfiguration(1, config));
    }
}