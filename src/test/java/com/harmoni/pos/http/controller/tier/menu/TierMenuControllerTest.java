package com.harmoni.pos.http.controller.tier.menu;

import com.harmoni.pos.business.service.tier.tiermenu.TierMenuService;
import com.harmoni.pos.menu.model.TierMenu;
import com.harmoni.pos.menu.model.TierType;
import com.harmoni.pos.menu.model.dto.add.TierMenuEditDto;
import com.harmoni.pos.menu.model.dto.edit.CategoryEditDto;
import com.harmoni.pos.menu.model.dto.edit.TierEditDto;
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
class TierMenuControllerTest {

    @Mock
    private TierMenuService tierMenuService;

    @InjectMocks
    private TierMenuController tierMenuController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tierMenuController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void update_shouldReturn201() throws Exception {
        TierMenuEditDto dto = new TierMenuEditDto();
        TierEditDto tierDto = new TierEditDto();
        tierDto.setId(1);
        tierDto.setName("Gold");
        tierDto.setBrandId(1);
        tierDto.setType(TierType.PRICE);
        dto.setTierDto(tierDto);
        CategoryEditDto categoryDto = new CategoryEditDto();
        categoryDto.setId(1);
        categoryDto.setName("Drinks");
        categoryDto.setDescription("Beverages");
        categoryDto.setBrandId(1);
        dto.setCategoryDto(categoryDto);
        dto.setActive(true);
        when(tierMenuService.create(eq(1), anyList())).thenReturn(1);

        mockMvc.perform(put("/api/v1/tier/1/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isCreated());
    }

    @Test
    void getByBrandId_shouldReturn200() throws Exception {
        when(tierMenuService.getMenusByBrandId(1)).thenReturn(List.of(new TierMenu().setId(1)));

        mockMvc.perform(get("/api/v1/tier/menu")
                        .param("brandId", "1"))
                .andExpect(status().isOk());
    }
}
