package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity representing the price of a SKU for a specific Tier.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkuTierPrice {
    /**
     * The unique identifier of the SKU tier price.
     */
    private Integer id;

    /**
     * The SKU ID associated with this price.
     */
    private Integer skuId;

    /**
     * The Tier ID associated with this price.
     */
    private Integer tierId;

    /**
     * The Tier entity associated with this price.
     */
    private Tier tier;

    /**
     * The price for the SKU and Tier.
     */
    private BigDecimal price;

    /**
     * Indicates if the SKU tier price is deleted.
     */
    private Boolean deleted;

    /**
     * The creation timestamp of the SKU tier price.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the SKU tier price.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the SKU tier price.
     */
    private Date deletedAt;

}