package com.harmoni.pos.http.controller.store;

import com.harmoni.pos.business.service.store.StoreService;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.dto.StoreDto;
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

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest {

    @Mock
    private StoreService storeService;

    @InjectMocks
    private StoreController storeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(storeController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        StoreDto dto = new StoreDto();
        dto.setName("Store1");
        dto.setChainId(1);
        dto.setAddress("123 Main St");
        dto.setTierMenuId(1);
        dto.setTierPriceId(1);
        dto.setTierServiceId(1);
        when(storeService.create(any(StoreDto.class))).thenReturn(1);

        mockMvc.perform(post("/api/v1/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void list_shouldReturn200() throws Exception {
        when(storeService.getAllStoresByChainIdPaginated(1, 1, 10, "search"))
                .thenReturn(Map.of("data", "result"));

        mockMvc.perform(get("/api/v1/store")
                        .param("chainId", "1")
                        .param("page", "1")
                        .param("size", "10")
                        .param("search", "search"))
                .andExpect(status().isOk());
    }

    @Test
    void get_shouldReturn200() throws Exception {
        when(storeService.get(1)).thenReturn(new Store().setId(1).setName("Store1"));

        mockMvc.perform(get("/api/v1/store/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Store1"));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        StoreDto dto = new StoreDto();
        dto.setName("Store1");
        dto.setChainId(1);
        dto.setAddress("123 Main St");
        dto.setTierMenuId(1);
        dto.setTierPriceId(1);
        dto.setTierServiceId(1);
        when(storeService.update(eq(1), any(StoreDto.class))).thenReturn(1);

        mockMvc.perform(put("/api/v1/store/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        when(storeService.delete(1)).thenReturn(1);

        mockMvc.perform(delete("/api/v1/store/1"))
                .andExpect(status().isOk());
    }
}
