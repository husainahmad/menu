package com.harmoni.pos.business.service.product.image;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.ProductImageMapper;
import com.harmoni.pos.menu.model.ProductImage;
import com.harmoni.pos.menu.model.dto.ProductImageDto;
import com.harmoni.pos.menu.model.dto.edit.ProductImageEditDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceImplTest {

    private static final byte[] IMAGE_BYTES = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==");

    @Mock
    private ProductImageMapper productImageMapper;

    @Mock
    private ImgbbUploadService imgbbUploadService;

    @InjectMocks
    private ProductImageServiceImpl productImageService;

    private ProductImage productImage;

    @BeforeEach
    void setUp() {
        productImage = new ProductImage()
                .setId(1).setProductId(10)
                .setFileName("test.jpg")
                .setUrl("http://imgbb.test/x.jpg");
    }

    @Test
    void deleteByPrimaryKey_shouldReturnRows() {
        when(productImageMapper.deleteByPrimaryKey(1)).thenReturn(1);
        assertEquals(1, productImageService.deleteByPrimaryKey(1));
    }

    @Test
    void selectByPrimaryKey_shouldReturnProductImage() {
        when(productImageMapper.selectByPrimaryKey(1)).thenReturn(productImage);
        assertNotNull(productImageService.selectByPrimaryKey(1));
    }

    @Test
    void selectByPrimaryKey_shouldThrow_whenNotFound() {
        when(productImageMapper.selectByPrimaryKey(1)).thenReturn(null);
        assertThrows(BusinessBadRequestException.class, () -> productImageService.selectByPrimaryKey(1));
    }

    @Test
    void selectByProductId_shouldReturnImage() {
        when(productImageMapper.selectByProductKey(10)).thenReturn(productImage);
        assertNotNull(productImageService.selectByProductId(10));
    }

    @Test
    void insert_shouldUploadToImgbbAndReturnProductImage() throws Exception {
        ProductImageDto dto = new ProductImageDto();
        dto.setProductId(10);
        dto.setFileName("x.jpg");
        dto.setMimeType("image/jpeg");
        when(imgbbUploadService.upload(any(byte[].class), anyString())).thenReturn("http://imgbb.test/y.jpg");
        when(productImageMapper.insert(any(ProductImage.class))).thenReturn(1);

        ProductImage result = productImageService.insert(dto, IMAGE_BYTES);

        assertEquals("http://imgbb.test/y.jpg", result.getUrl());
        verify(productImageMapper).insert(any(ProductImage.class));
    }

    @Test
    void updateByProductId_shouldUpdate_whenRecordExists() {
        ProductImageEditDto editDto = new ProductImageEditDto();
        editDto.setId(1);
        when(productImageMapper.updateProductIdByPrimaryKey(any(ProductImage.class))).thenReturn(1);

        int result = productImageService.updateByProductId(10, editDto);
        assertEquals(1, result);
    }

    @Test
    void updateByProductId_shouldReturnZero_whenDtoIsNull() {
        assertEquals(0, productImageService.updateByProductId(10, null));
    }

    @Test
    void updateByProductId_shouldInsert_whenNoExistingRow() {
        ProductImageEditDto editDto = new ProductImageEditDto();
        editDto.setId(1);
        when(productImageMapper.updateProductIdByPrimaryKey(any(ProductImage.class))).thenReturn(0);
        when(productImageMapper.insert(any(ProductImage.class))).thenReturn(1);

        int result = productImageService.updateByProductId(10, editDto);
        assertEquals(1, result);
    }

    @Test
    void updateImageByProductId_shouldUpdate_whenExisting() throws Exception {
        ProductImageEditDto editDto = new ProductImageEditDto();
        editDto.setFileName("x.jpg");
        editDto.setMimeType("image/jpeg");
        when(productImageMapper.selectByProductKey(10)).thenReturn(productImage);
        when(productImageMapper.updateImageByProductKey(any(ProductImage.class))).thenReturn(1);
        when(imgbbUploadService.upload(any(byte[].class), anyString())).thenReturn("http://imgbb.test/x.jpg");

        ProductImage result = productImageService.updateImageByProductId(10, editDto, IMAGE_BYTES);
        assertNotNull(result);
        assertEquals("http://imgbb.test/x.jpg", result.getUrl());
    }
}
