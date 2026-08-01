package com.harmoni.pos.business.service.category;

import com.harmoni.pos.business.service.store.tier.StoreTierService;
import com.harmoni.pos.business.service.tier.tiermenu.TierMenuService;
import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.CategoryMapper;
import com.harmoni.pos.menu.model.*;
import com.harmoni.pos.menu.model.dto.CategoryDto;
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
class CategoryServiceImplTest {

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private UserService userService;

    @Mock
    private StoreTierService storeTierService;

    @Mock
    private TierMenuService tierMenuService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryDto categoryDto;
    private Category category;
    private User user;

    @BeforeEach
    void setUp() {
        categoryDto = new CategoryDto();
        categoryDto.setName("Drinks");
        categoryDto.setDescription("Beverages");
        categoryDto.setBrandId(1);

        category = new Category().setId(1).setName("Drinks").setBrandId(1);

        Brand brand = new Brand().setId(1);
        Chain chain = new Chain().setId(1).setBrandId(1).setBrand(brand);
        Store store = new Store().setId(1).setChain(chain);
        user = new User().setId(1).setStore(store).setStoreId(1);
    }

    @Test
    void create_shouldSucceed() {
        when(categoryMapper.selectByNameBrandId("Drinks", 1)).thenReturn(null);
        when(categoryMapper.insert(any(Category.class))).thenReturn(1);

        int result = categoryService.create(categoryDto);
        assertEquals(1, result);
    }

    @Test
    void create_shouldThrow_whenDuplicate() {
        when(categoryMapper.selectByNameBrandId("Drinks", 1)).thenReturn(category);
        assertThrows(BusinessBadRequestException.class, () -> categoryService.create(categoryDto));
    }

    @Test
    void create_shouldThrow_whenInsertFails() {
        when(categoryMapper.selectByNameBrandId("Drinks", 1)).thenReturn(null);
        when(categoryMapper.insert(any(Category.class))).thenReturn(0);
        assertThrows(BusinessNoContentRequestException.class, () -> categoryService.create(categoryDto));
    }

    @Test
    void delete_shouldSucceed() {
        when(categoryMapper.selectByPrimaryKey(1)).thenReturn(category);
        when(categoryMapper.deleteByPrimaryKey(1)).thenReturn(1);
        assertEquals(1, categoryService.delete(1));
    }

    @Test
    void get_shouldReturnCategory() {
        when(categoryMapper.selectByPrimaryKey(1)).thenReturn(category);
        assertNotNull(categoryService.get(1));
    }

    @Test
    void get_shouldThrow_whenNotFound() {
        when(categoryMapper.selectByPrimaryKey(1)).thenReturn(null);
        assertThrows(BusinessBadRequestException.class, () -> categoryService.get(1));
    }

    @Test
    void selectByBrandId_shouldReturnList() {
        when(categoryMapper.selectByBrandId(1)).thenReturn(List.of(category));
        assertFalse(categoryService.selectByBrandId(1).isEmpty());
    }

    @Test
    void getListByUserAuth_shouldReturnCategories() {
        StoreTier storeTier = new StoreTier().setTierMenuId(10);
        TierMenu tierMenu = new TierMenu().setCategory(category);

        when(userService.selectByUsername("Bearer token123")).thenReturn(user);
        when(storeTierService.selectByStoreId(1)).thenReturn(storeTier);
        when(tierMenuService.getMenusByTierId(10)).thenReturn(List.of(tierMenu));

        List<Category> result = categoryService.getListByUserAuth("Bearer token123");
        assertEquals(1, result.size());
        assertEquals("Drinks", result.getFirst().getName());
    }

    @Test
    void getListByUserAuth_shouldReturnByUsername() {
        StoreTier storeTier = new StoreTier().setTierMenuId(10);
        TierMenu tierMenu = new TierMenu().setCategory(category);

        when(userService.selectByUsername("Bearer token123")).thenReturn(user);
        when(storeTierService.selectByStoreId(1)).thenReturn(storeTier);
        when(tierMenuService.getMenusByTierId(10)).thenReturn(List.of(tierMenu));

        List<Category> result = categoryService.getListByUserAuth("Bearer token123");
        assertEquals(1, result.size());
        verify(userService).selectByUsername("Bearer token123");
    }
}
