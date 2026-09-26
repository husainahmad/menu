package com.harmoni.pos.http.controller.promotion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.harmoni.pos.business.service.promotion.orderitemdiscount.OrderItemDiscountService;
import com.harmoni.pos.menu.model.DiscountType;
import com.harmoni.pos.menu.model.OrderItemDiscount;
import com.harmoni.pos.menu.model.dto.OrderItemDiscountDto;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderItemDiscountControllerTest {

    @Mock
    private OrderItemDiscountService orderItemDiscountService;

    @InjectMocks
    private OrderItemDiscountController orderItemDiscountController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderItemDiscountController).build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    private static OrderItemDiscountDto dto() {
        OrderItemDiscountDto dto = new OrderItemDiscountDto();
        dto.setOrderItemId(9001L);
        dto.setPromotionId(1L);
        dto.setDiscountType(DiscountType.PERCENTAGE);
        dto.setDiscountValue(new BigDecimal("10.00"));
        dto.setDiscountAmount(new BigDecimal("1000.00"));
        return dto;
    }

    @Test
    void create_shouldReturn201() throws Exception {
        when(orderItemDiscountService.create(any(OrderItemDiscountDto.class)))
                .thenReturn(new OrderItemDiscount().setId(1L).setOrderItemId(9001L));

        mockMvc.perform(post("/api/v1/order-item-discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.httpStatus").value(201))
                .andExpect(jsonPath("$.data.orderItemId").value(9001));
    }

    @Test
    void create_shouldReturn400_whenOrderItemIdMissing() throws Exception {
        OrderItemDiscountDto invalid = dto();
        invalid.setOrderItemId(null);

        mockMvc.perform(post("/api/v1/order-item-discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBulk_shouldReturn201() throws Exception {
        when(orderItemDiscountService.createBulk(any()))
                .thenReturn(List.of(new OrderItemDiscount().setId(1L), new OrderItemDiscount().setId(2L)));

        mockMvc.perform(post("/api/v1/order-item-discount/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto(), dto()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getByOrderItemId_shouldReturn200() throws Exception {
        when(orderItemDiscountService.getByOrderItemId(9001L))
                .thenReturn(List.of(new OrderItemDiscount().setId(1L).setPromotionCode("HAPPY10")));

        mockMvc.perform(get("/api/v1/order-item-discount/order-item/9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].promotionCode").value("HAPPY10"));
    }

    @Test
    void getRedemptionSummary_shouldReturn200() throws Exception {
        when(orderItemDiscountService.getRedemptionSummary(eq(1L), isNull(), isNull()))
                .thenReturn(List.of(new OrderItemDiscount().setOrderItemId(9001L)));

        mockMvc.perform(get("/api/v1/order-item-discount/promotion/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void deleteByOrderItemId_shouldReturn200() throws Exception {
        when(orderItemDiscountService.deleteByOrderItemId(9001L)).thenReturn(2);

        mockMvc.perform(delete("/api/v1/order-item-discount/order-item/9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(2));
    }
}
