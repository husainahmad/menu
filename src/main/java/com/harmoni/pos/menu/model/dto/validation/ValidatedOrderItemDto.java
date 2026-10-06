package com.harmoni.pos.menu.model.dto.validation;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * One confirmed line: the official menu data behind the caller's IDs, plus the quantities
 * the caller asked for, validated.
 * <p>
 * Everything a receipt would need is here, so the order service can store the line as it
 * was sold without looking anything up again. A name or price read now is what was on
 * sale then; re-reading the catalogue at print time would silently reprint yesterday's
 * menu.
 * <p>
 * There is no subtotal or line total here on purpose. See {@link ValidatedCustomizationDto}.
 *
 * @author husainahmad
 */
@Data
public class ValidatedOrderItemDto {

    /**
     * The product ordered, echoed back so a caller validating a basket can match answers
     * to requests without relying on ordering.
     */
    private Integer productId;

    /**
     * The product name as it is currently configured.
     */
    private String productName;

    /**
     * The category the product is filed under, echoed for the same reason as
     * {@link #productId}: an order line is snapshotted with its category so a promotion
     * can be scoped to a category later, without the catalogue changing underneath it.
     */
    private Integer categoryId;

    /**
     * The category name as it is currently configured.
     */
    private String categoryName;

    /**
     * The SKU ordered.
     */
    private Integer skuId;

    /**
     * The SKU name as it is currently configured.
     */
    private String skuName;

    /**
     * The price of the SKU for one unit, at the store's tier.
     */
    private BigDecimal skuPrice;

    /**
     * The validated number of units ordered.
     */
    private Integer quantity;

    /**
     * The customization choices on the line, named and priced.
     */
    private List<ValidatedCustomizationDto> customizations = new ArrayList<>();
}