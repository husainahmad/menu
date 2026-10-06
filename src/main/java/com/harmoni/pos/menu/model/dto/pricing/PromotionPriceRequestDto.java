package com.harmoni.pos.menu.model.dto.pricing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * A request to price a basket of lines against the live promotion catalogue.
 * <p>
 * This is the entry point an order service calls when a basket is confirmed. It is a
 * pure price request: nothing is persisted, and nothing in the request can influence
 * what the promotions are worth. The caller supplies only what it is selling, and the
 * engine decides the discount.
 *
 * @author husainahmad
 */
@Data
public class PromotionPriceRequestDto {

    @NotEmpty(message = "{validation.promotionPrice.lines.NotEmpty}")
    @Valid
    private List<CartLineDto> lines;

    @NotNull(message = "{validation.promotionPrice.context.NotNull}")
    @Valid
    private PromotionContextDto context;
}
