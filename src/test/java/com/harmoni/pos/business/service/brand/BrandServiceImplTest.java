package com.harmoni.pos.business.service.brand;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.menu.mapper.BrandMapper;
import com.harmoni.pos.menu.model.Brand;
import com.harmoni.pos.menu.model.dto.BrandDto;
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
class BrandServiceImplTest {

    @Mock
    private BrandMapper brandMapper;

    @InjectMocks
    private BrandServiceImpl brandService;

    private BrandDto brandDto;
    private Brand brand;

    @BeforeEach
    void setUp() {
        brandDto = new BrandDto();
        brandDto.setName("Test Brand");

        brand = new Brand().setId(1).setName("Test Brand");
    }

    @Test
    void create_shouldSucceed() {
        when(brandMapper.selectByName("Test Brand")).thenReturn(null);
        when(brandMapper.insert(any(Brand.class))).thenReturn(1);

        int result = brandService.create(brandDto);

        assertEquals(1, result);
        verify(brandMapper).selectByName("Test Brand");
        verify(brandMapper).insert(any(Brand.class));
    }

    @Test
    void create_shouldThrow_whenDuplicate() {
        when(brandMapper.selectByName("Test Brand")).thenReturn(brand);

        assertThrows(BusinessBadRequestException.class, () -> brandService.create(brandDto));
        verify(brandMapper, never()).insert(any());
    }

    @Test
    void create_shouldThrow_whenInsertFails() {
        when(brandMapper.selectByName("Test Brand")).thenReturn(null);
        when(brandMapper.insert(any(Brand.class))).thenReturn(0);

        assertThrows(BusinessNoContentRequestException.class, () -> brandService.create(brandDto));
    }

    @Test
    void delete_shouldSucceed() {
        when(brandMapper.selectByPrimaryKey(1)).thenReturn(brand);
        when(brandMapper.deleteByPrimaryKey(1)).thenReturn(1);

        int result = brandService.delete(1);

        assertEquals(1, result);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(brandMapper.selectByPrimaryKey(1)).thenReturn(null);

        assertThrows(BusinessNotFoundRequestException.class, () -> brandService.delete(1));
    }

    @Test
    void get_shouldSucceed() {
        when(brandMapper.selectByPrimaryKey(1)).thenReturn(brand);

        Brand result = brandService.get(1);

        assertNotNull(result);
        assertEquals("Test Brand", result.getName());
    }

    @Test
    void get_shouldThrow_whenNotFound() {
        when(brandMapper.selectByPrimaryKey(1)).thenReturn(null);

        assertThrows(BusinessNotFoundRequestException.class, () -> brandService.get(1));
    }

    @Test
    void list_shouldReturnAll() {
        when(brandMapper.selectAll()).thenReturn(List.of(brand));

        List<Brand> result = brandService.list();

        assertEquals(1, result.size());
    }
}
