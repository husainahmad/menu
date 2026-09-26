package com.harmoni.pos.http.controller.promotion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.harmoni.pos.business.service.promotion.PromotionService;
import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.PromotionStatus;
import com.harmoni.pos.menu.model.PromotionType;
import com.harmoni.pos.menu.model.dto.add.PromotionAddDto;
import com.harmoni.pos.menu.model.dto.edit.PromotionEditDto;
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
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PromotionControllerTest {

    @Mock
    private PromotionService promotionService;

    @InjectMocks
    private PromotionController promotionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(promotionController).build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    private static PromotionAddDto addDto() {
        PromotionAddDto dto = new PromotionAddDto();
        dto.setCode("HAPPY10");
        dto.setName("Happy Hour");
        dto.setPromotionType(PromotionType.PERCENTAGE);
        dto.setStatus(PromotionStatus.ACTIVE);
        return dto;
    }

    private static PromotionEditDto editDto() {
        PromotionEditDto dto = new PromotionEditDto();
        dto.setId(1L);
        dto.setCode("HAPPY10");
        dto.setName("Happy Hour");
        dto.setPromotionType(PromotionType.PERCENTAGE);
        dto.setStatus(PromotionStatus.ACTIVE);
        return dto;
    }

    @Test
    void create_shouldReturn201() throws Exception {
        when(promotionService.create(any(PromotionAddDto.class)))
                .thenReturn(new Promotion().setId(1L).setCode("HAPPY10"));

        mockMvc.perform(post("/api/v1/promotion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.httpStatus").value(201))
                .andExpect(jsonPath("$.data.code").value("HAPPY10"));
    }

    @Test
    void create_shouldReturn400_whenCodeMissing() throws Exception {
        PromotionAddDto dto = addDto();
        dto.setCode(null);

        mockMvc.perform(post("/api/v1/promotion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void list_shouldReturn200() throws Exception {
        when(promotionService.listPaginated(PromotionStatus.ACTIVE, PromotionType.PERCENTAGE, "happy", 1, 10))
                .thenReturn(Map.of("data", List.of(), "total", 0));

        mockMvc.perform(get("/api/v1/promotion")
                        .param("status", "ACTIVE")
                        .param("promotionType", "PERCENTAGE")
                        .param("search", "happy")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    void list_shouldReturn200_whenFiltersOmitted() throws Exception {
        when(promotionService.listPaginated(null, null, null, 1, 10))
                .thenReturn(Map.of("data", List.of()));

        mockMvc.perform(get("/api/v1/promotion").param("page", "1").param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void list_shouldReturn400_whenStatusUnknown() throws Exception {
        mockMvc.perform(get("/api/v1/promotion")
                        .param("status", "NOPE")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_shouldReturn200() throws Exception {
        when(promotionService.get(1L)).thenReturn(new Promotion().setId(1L).setName("Happy Hour"));

        mockMvc.perform(get("/api/v1/promotion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Happy Hour"));
    }

    @Test
    void getByCode_shouldReturn200() throws Exception {
        when(promotionService.getByCode("HAPPY10"))
                .thenReturn(new Promotion().setId(1L).setCode("HAPPY10"));

        mockMvc.perform(get("/api/v1/promotion/code/HAPPY10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("HAPPY10"));
    }

    @Test
    void listRedeemable_shouldReturn200() throws Exception {
        when(promotionService.listRedeemable()).thenReturn(List.of(new Promotion().setId(1L)));

        mockMvc.perform(get("/api/v1/promotion/redeemable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void put_shouldReturn200() throws Exception {
        when(promotionService.update(any(PromotionEditDto.class)))
                .thenReturn(new Promotion().setId(1L).setName("Happy Hour v2"));

        mockMvc.perform(put("/api/v1/promotion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Happy Hour v2"));
    }

    @Test
    void put_shouldReturn400_whenIdMissing() throws Exception {
        PromotionEditDto dto = editDto();
        dto.setId(null);

        mockMvc.perform(put("/api/v1/promotion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchStatus_shouldReturn200() throws Exception {
        when(promotionService.updateStatus(1L, PromotionStatus.PAUSED)).thenReturn(1);

        mockMvc.perform(patch("/api/v1/promotion/1/status").param("status", "PAUSED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    void patchStatus_shouldReturn400_whenStatusUnknown() throws Exception {
        mockMvc.perform(patch("/api/v1/promotion/1/status").param("status", "NOPE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        when(promotionService.delete(1L)).thenReturn(1);

        mockMvc.perform(delete("/api/v1/promotion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    void deleteByFilter_shouldReturn200() throws Exception {
        when(promotionService.deleteByFilter(PromotionStatus.EXPIRED, null, "old")).thenReturn(3);

        mockMvc.perform(delete("/api/v1/promotion")
                        .param("status", "EXPIRED")
                        .param("search", "old"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(3));
    }
}
