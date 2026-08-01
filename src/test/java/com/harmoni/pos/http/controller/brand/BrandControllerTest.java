package com.harmoni.pos.http.controller.brand;

import com.harmoni.pos.business.service.brand.BrandService;
import com.harmoni.pos.menu.model.Brand;
import com.harmoni.pos.menu.model.dto.BrandDto;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BrandControllerTest {

    @Mock
    private BrandService brandService;

    @InjectMocks
    private BrandController brandController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(brandController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        BrandDto dto = new BrandDto();
        dto.setName("Test Brand");
        when(brandService.create(any(BrandDto.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void all_shouldReturn200() throws Exception {
        when(brandService.list()).thenReturn(List.of(new Brand().setId(1).setName("Brand1")));

        mockMvc.perform(get("/api/v1/brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data[0].name").value("Brand1"));
    }

    @Test
    void get_shouldReturn200() throws Exception {
        when(brandService.get(1)).thenReturn(new Brand().setId(1).setName("Brand1"));

        mockMvc.perform(get("/api/v1/brand/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Brand1"));
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        when(brandService.delete(1)).thenReturn(1);

        mockMvc.perform(delete("/api/v1/brand/1"))
                .andExpect(status().isOk());
    }
}
