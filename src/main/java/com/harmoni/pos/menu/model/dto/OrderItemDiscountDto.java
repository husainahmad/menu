package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.DiscountType;
import com.harmoni.pos.menu.model.OrderItemDiscount;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data transfer object for recording a discount against a single order line.
 * <p>
 * The promotion attributes are optional because a {@link DiscountType#MANUAL}
 * discount is not linked to any promotion. When {@code promotionId} is supplied
 * the service copies {@code promotionCode} and {@code promotionName} from the
 * promotion so the snapshot stays readable after the promotion changes.
 */
@Data
public class OrderItemDiscountDto {

    @NotNull(message = "{validation.orderItemDiscount.orderItemId.NotNull}")
    private Long orderItemId;

    private Long promotionId;

    @NotNull(message = "{validation.orderItemDiscount.discountType.NotNull}")
    private DiscountType discountType;

    @NotNull(message = "{validation.orderItemDiscount.discountValue.NotNull}")
    @PositiveOrZero(message = "{validation.orderItemDiscount.discountValue.PositiveOrZero}")
    private BigDecimal discountValue;

    @NotNull(message = "{validation.orderItemDiscount.discountAmount.NotNull}")
    @PositiveOrZero(message = "{validation.orderItemDiscount.discountAmount.PositiveOrZero}")
    private BigDecimal discountAmount;

    /**
     * Converts this DTO to an OrderItemDiscount entity, timestamp left unset.
     *
     * @return an OrderItemDiscount entity carrying the caller supplied values
     */
    public OrderItemDiscount toOrderItemDiscount() {
        return new OrderItemDiscount()
                .setOrderItemId(orderItemId)
                .setPromotionId(promotionId)
                .setDiscountType(discountType)
                .setDiscountValue(discountValue)
                .setDiscountAmount(discountAmount);
    }
}
