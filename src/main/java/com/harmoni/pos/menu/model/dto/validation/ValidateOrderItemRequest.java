package com.harmoni.pos.menu.model.dto.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * One line of a basket as the order service sent it: the product and SKU chosen, how many
 * units, and the customization choices made on the line.
 * <p>
 * Only IDs and quantities appear here. Names and prices are deliberately absent from the
 * request, because the order service does not get to decide what a product is called or
 * what it costs; it asks, and this service answers from the catalogue.
 *
 * @author husainahmad
 */
@Data
public class ValidateOrderItemRequest {

    /**
     * The product being ordered.
     */
    @NotNull(message = "{validation.orderValidation.productId.NotNull}")
    private Integer productId;

    /**
     * The SKU being ordered. Must belong to {@link #productId}.
     */
    @NotNull(message = "{validation.orderValidation.skuId.NotNull}")
    private Integer skuId;

    /**
     * How many units of the SKU are being ordered.
     * <p>
     * Deliberately unannotated: whether a quantity is legal is a selling rule, decided
     * here alongside the rest of them, so that every rejected quantity reports the same
     * reason.
     */
    private Integer quantity;

    /**
     * The customization groups chosen on the line, each with its selected options.
     * May be empty, but only when the product has no customization requiring a choice.
     */
    @Valid
    private List<CustomizationGroupSelectionDto> customizations = new ArrayList<>();
}