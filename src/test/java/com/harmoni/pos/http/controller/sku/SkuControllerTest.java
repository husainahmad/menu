package com.harmoni.pos.http.controller.sku;

import com.harmoni.pos.business.service.sku.SkuService;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.dto.add.SkuAddDto;
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
class SkuControllerTest {

    @Mock
    private SkuService skuService;

    @InjectMocks
    private SkuController skuController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(skuController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        SkuAddDto dto = new SkuAddDto();
        dto.setName("Small");
        dto.setProductId(1);
        SkuTierPriceDto tierPrice = new SkuTierPriceDto();
        tierPrice.setTierId(1);
        tierPrice.setPrice(BigDecimal.TEN);
        dto.setSkuTierPriceDtos(List.of(tierPrice));
        when(skuService.create(any(SkuAddDto.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/sku")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(skuService).deleteSku(1);

        mockMvc.perform(delete("/api/v1/sku/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getDetails_shouldReturn200() throws Exception {
        when(skuService.selectPriceByIds("ahmad.husain", List.of(1, 2)))
                .thenReturn(List.of(new Sku().setId(1)));

        mockMvc.perform(get("/api/v1/sku/price")
                        .header("X-Username", "ahmad.husain")
                        .param("ids", "1", "2"))
                .andExpect(status().isOk());
    }
}
