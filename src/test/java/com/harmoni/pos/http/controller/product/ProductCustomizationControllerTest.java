package com.harmoni.pos.http.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harmoni.pos.business.service.product.ProductCustomizationService;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationConfigDto;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationReplaceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductCustomizationControllerTest {

    @Mock
    private ProductCustomizationService productCustomizationService;

    @InjectMocks
    private ProductCustomizationController productCustomizationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productCustomizationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void list_shouldReturn200() throws Exception {
        when(productCustomizationService.getDetailedByProductId(10)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/product/10/customization"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void replace_shouldReturn200() throws Exception {
        ProductCustomizationReplaceDto replace = new ProductCustomizationReplaceDto();
        replace.setCustomizationIds(List.of(20, 30));
        when(productCustomizationService.replaceForProduct(10, List.of(20, 30))).thenReturn(2);

        mockMvc.perform(put("/api/v1/product/10/customization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replace)))
                .andExpect(status().isOk());
        verify(productCustomizationService).replaceForProduct(10, List.of(20, 30));
    }

    @Test
    void replace_emptyIds_shouldReturn400() throws Exception {
        ProductCustomizationReplaceDto replace = new ProductCustomizationReplaceDto();
        replace.setCustomizationIds(List.of());

        mockMvc.perform(put("/api/v1/product/10/customization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replace)))
                .andExpect(status().isBadRequest());
        verify(productCustomizationService, never()).replaceForProduct(anyInt(), anyList());
    }

    @Test
    void configure_shouldReturn200() throws Exception {
        ProductCustomizationConfigDto config = new ProductCustomizationConfigDto();
        config.setRequiredOverride(true);
        config.setMinSelectionOverride(1);
        config.setMaxSelectionOverride(3);
        when(productCustomizationService.updateConfiguration(5, config)).thenReturn(1);

        mockMvc.perform(put("/api/v1/product/10/customization/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(config)))
                .andExpect(status().isOk());
    }

    @Test
    void detach_shouldReturn200() throws Exception {
        when(productCustomizationService.delete(5)).thenReturn(1);

        mockMvc.perform(delete("/api/v1/product/10/customization/5"))
                .andExpect(status().isOk());
        verify(productCustomizationService).delete(5);
    }
}