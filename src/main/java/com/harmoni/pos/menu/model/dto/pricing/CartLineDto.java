package com.harmoni.pos.menu.model.dto.pricing;

import com.harmoni.pos.business.service.promotion.engine.CartLine;
import lombok.Data;

import java.math.BigDecimal;

/**
 * A single sale line as sent by a caller asking for a price.
 * <p>
 * The line is identified by {@code lineIndex}, the caller's own position in the cart,
 * rather than by a database ID. A caller prices a basket before the order is persisted,
 * so no order line ID exists yet; the index is echoed back on the priced line so the
 * caller can match results to its own lines without depending on the order the engine
 * happens to process them in.
 *
 * @author husainahmad
 */
@Data
public class CartLineDto {

    /**
     * The caller's zero based position of this line within the cart.
     */
    private Integer lineIndex;

    private Long productId;

    private Long skuId;

    private Long categoryId;

    /**
     * The pre-discount price charged for one unit.
     */
    private BigDecimal unitPrice;

    /**
     * How many units the line carries. Decimal so weight based lines such as
     * "0.5 kg of mince" are legal.
     */
    private BigDecimal quantity;

    /**
     * Converts this DTO to the engine's cart line, leaving the order item ID unset
     * because the order has not been persisted yet.
     *
     * @return the engine cart line
     */
    public CartLine toCartLine() {
        return new CartLine(null, productId, skuId, categoryId, unitPrice, quantity);
    }
}
