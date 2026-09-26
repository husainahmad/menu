package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Entity representing a Promotion, the header of the promotion aggregate.
 * <p>
 * A promotion is only ever evaluated when its {@link #status} is
 * {@link PromotionStatus#ACTIVE}, the current date falls inside
 * {@link #startDate}..{@link #endDate} and the current time falls inside one of
 * the {@link #schedules} windows. The four child collections carry the pricing
 * logic and are populated on demand by
 * {@code PromotionService#get(Long)}, never by the paginated list endpoint.
 *
 * @author husainahmad
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Promotion {

    /**
     * The unique identifier of the promotion.
     */
    private Long id;

    /**
     * The unique, human readable code used to redeem the promotion at checkout.
     */
    private String code;

    /**
     * The display name of the promotion.
     */
    private String name;

    /**
     * The optional description shown to staff and on reports.
     */
    private String description;

    /**
     * The pricing mechanism, see {@link PromotionType}.
     */
    private PromotionType promotionType;

    /**
     * The lifecycle state, see {@link PromotionStatus}.
     */
    private PromotionStatus status;

    /**
     * Evaluation order; the lowest value is applied first when several
     * promotions compete for the same line.
     */
    private Integer priority;

    /**
     * The first date on which the promotion may be redeemed, inclusive.
     */
    private LocalDate startDate;

    /**
     * The last date on which the promotion may be redeemed, inclusive.
     */
    private LocalDate endDate;

    /**
     * The time windows during which the promotion is redeemable.
     */
    @JsonProperty("schedules")
    private List<PromotionSchedule> schedules;

    /**
     * The catalog items the promotion applies to.
     */
    @JsonProperty("targets")
    private List<PromotionTarget> targets;

    /**
     * The conditions and caps applied when resolving the discount.
     */
    @JsonProperty("rules")
    private List<PromotionRule> rules;

    /**
     * The per-SKU price overrides, only used by
     * {@link PromotionType#SPECIAL_PRICE} promotions.
     */
    @JsonProperty("specialPrices")
    private List<PromotionSpecialPrice> specialPrices;

    /**
     * The creation timestamp of the promotion.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the promotion.
     */
    private Date updatedAt;
}
