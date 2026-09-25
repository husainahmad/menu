package com.harmoni.pos.http.controller.product.image;

import com.harmoni.pos.business.service.product.image.ProductImageService;
import com.harmoni.pos.menu.model.ProductImage;
import com.harmoni.pos.menu.model.dto.ProductImageDto;
import com.harmoni.pos.menu.model.dto.edit.ProductImageEditDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductImageControllerTest {

    @Mock
    private ProductImageService productImageService;

    @InjectMocks
    private ProductImageController productImageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productImageController).build();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "test-image-content".getBytes());
        when(productImageService.insert(any(ProductImageDto.class), any(byte[].class))).thenReturn(new ProductImage().setId(1));

        mockMvc.perform(multipart("/api/v1/product/image/upload")
                        .file(file))
                .andExpect(status().isCreated());
    }

    @Test
    void update_shouldReturn201() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "test-image-content".getBytes());
        when(productImageService.updateImageByProductId(eq(1), any(ProductImageEditDto.class), any(byte[].class)))
                .thenReturn(new ProductImage().setId(1).setProductId(1));

        mockMvc.perform(multipart("/api/v1/product/image/1/upload")
                        .file(file)
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isCreated());
    }

    @Test
    void get_shouldReturn200() throws Exception {
        when(productImageService.selectByPrimaryKey(1)).thenReturn(new ProductImage().setId(1));

        mockMvc.perform(get("/api/v1/product/image/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
