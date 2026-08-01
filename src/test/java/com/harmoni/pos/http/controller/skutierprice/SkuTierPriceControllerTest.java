package com.harmoni.pos.http.controller.skutierprice;

import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.menu.model.SkuTierPrice;
import com.harmoni.pos.menu.model.dto.SkuTierPriceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SkuTierPriceControllerTest {

    @Mock
    private SkuTierPriceService skuTierPriceService;

    @InjectMocks
    private SkuTierPriceController skuTierPriceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(skuTierPriceController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        SkuTierPriceDto dto = new SkuTierPriceDto();
        dto.setSkuId(1);
        dto.setTierId(1);
        dto.setPrice(BigDecimal.TEN);
        when(skuTierPriceService.create(any(SkuTierPriceDto.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/skutier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void listBySku_shouldReturn200() throws Exception {
        when(skuTierPriceService.selectBySkusTierId(List.of(1, 2), 1))
                .thenReturn(List.of(new SkuTierPrice().setSkuId(1)));

        mockMvc.perform(get("/api/v1/skutier")
                        .param("skuIds", "1", "2")
                        .param("tierId", "1"))
                .andExpect(status().isOk());
    }
}
