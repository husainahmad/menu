package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Entity representing a catalog item a {@link Promotion} applies to.
 * <p>
 * The {@link #targetType} decides which of the three nullable references is
 * authoritative; the others are expected to stay {@code null}. A SKU target is the
 * most specific and therefore wins over a product target, which in turn wins over
 * a category target when several promotions overlap on the same cart line.
 *
 * @author husainahmad
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionTarget {

    /**
     * The unique identifier of the target.
     */
    private Long id;

    /**
     * The promotion this target belongs to.
     */
    private Long promotionId;

    /**
     * The catalog level being targeted, see {@link PromotionTargetType}.
     */
    private PromotionTargetType targetType;

    /**
     * The targeted product, set when {@link #targetType} is
     * {@link PromotionTargetType#PRODUCT}.
     */
    private Long productId;

    /**
     * The targeted SKU, set when {@link #targetType} is
     * {@link PromotionTargetType#SKU}.
     */
    private Long skuId;

    /**
     * The targeted category, set when {@link #targetType} is
     * {@link PromotionTargetType#CATEGORY}.
     */
    private Long categoryId;
}
