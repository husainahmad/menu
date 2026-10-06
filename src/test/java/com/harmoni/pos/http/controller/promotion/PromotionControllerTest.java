package com.harmoni.pos.http.controller.promotion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.harmoni.pos.business.service.promotion.PromotionService;
import com.harmoni.pos.business.service.promotion.engine.TargetMatch;
import com.harmoni.pos.business.service.promotion.pricing.PromotionPricingService;
import com.harmoni.pos.menu.model.DiscountType;
import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.PromotionStatus;
import com.harmoni.pos.menu.model.PromotionType;
import com.harmoni.pos.menu.model.dto.add.PromotionAddDto;
import com.harmoni.pos.menu.model.dto.edit.PromotionEditDto;
import com.harmoni.pos.menu.model.dto.pricing.AppliedDiscountDto;
import com.harmoni.pos.menu.model.dto.pricing.CartLineDto;
import com.harmoni.pos.menu.model.dto.pricing.PricedLineDto;
import com.harmoni.pos.menu.model.dto.pricing.PromotionContextDto;
import com.harmoni.pos.menu.model.dto.pricing.PromotionPriceRequestDto;
import com.harmoni.pos.menu.model.dto.pricing.PromotionPriceResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PromotionControllerTest {

    @Mock
    private PromotionService promotionService;

    @Mock
    private PromotionPricingService promotionPricingService;

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

    @Test
    void price_shouldReturn200WithAPriceForEveryLine() throws Exception {
        when(promotionPricingService.price(any())).thenReturn(priceResponse());

        mockMvc.perform(post("/api/v1/promotion/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priceRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.lines.length()").value(1))
                .andExpect(jsonPath("$.data.lines[0].netAmount").value(18.0))
                .andExpect(jsonPath("$.data.totalDiscount").value(2.0));
    }

    @Test
    void price_shouldRejectABasketWithNoLines() throws Exception {
        PromotionPriceRequestDto request = priceRequest();
        request.setLines(List.of());

        mockMvc.perform(post("/api/v1/promotion/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(promotionPricingService, never()).price(any());
    }

    private static PromotionPriceRequestDto priceRequest() {
        CartLineDto line = new CartLineDto();
        line.setLineIndex(0);
        line.setProductId(10L);
        line.setSkuId(20L);
        line.setCategoryId(30L);
        line.setUnitPrice(new BigDecimal("20.00"));
        line.setQuantity(new BigDecimal("1"));

        PromotionContextDto context = new PromotionContextDto();
        context.setStoreId(7L);
        context.setZone("Australia/Sydney");

        PromotionPriceRequestDto request = new PromotionPriceRequestDto();
        request.setLines(List.of(line));
        request.setContext(context);
        return request;
    }

    private static PromotionPriceResponseDto priceResponse() {
        AppliedDiscountDto discount = new AppliedDiscountDto();
        discount.setPromotionId(99L);
        discount.setPromotionCode("HAPPY10");
        discount.setPromotionName("Happy Hour");
        discount.setDiscountType(DiscountType.PERCENTAGE);
        discount.setDiscountValue(new BigDecimal("10.00"));
        discount.setDiscountAmount(new BigDecimal("2.00"));
        discount.setTargetMatch(TargetMatch.SKU);

        PricedLineDto line = new PricedLineDto();
        line.setLineIndex(0);
        line.setGrossAmount(new BigDecimal("20.00"));
        line.setDiscountAmount(new BigDecimal("2.00"));
        line.setNetAmount(new BigDecimal("18.00"));
        line.setDiscounted(true);
        line.setDiscounts(List.of(discount));

        PromotionPriceResponseDto response = new PromotionPriceResponseDto();
        response.setLines(List.of(line));
        response.setTotalDiscount(new BigDecimal("2.00"));
        response.setTotalNet(new BigDecimal("18.00"));
        return response;
    }
}
