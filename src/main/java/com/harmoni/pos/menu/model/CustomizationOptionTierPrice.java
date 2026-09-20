package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity representing the price of a customization option for a specific Tier.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomizationOptionTierPrice {

    /**
     * The unique identifier of the customization option tier price.
     */
    private Integer id;

    /**
     * The customization option ID associated with this price.
     */
    private Integer customizationOptionId;

    /**
     * The Tier ID associated with this price.
     */
    private Integer tierId;

    /**
     * The Tier entity associated with this price.
     */
    private Tier tier;

    /**
     * The price for the customization option and Tier.
     */
    private BigDecimal price;

    /**
     * Indicates if the customization option tier price is deleted.
     */
    private Boolean deleted;

    /**
     * The creation timestamp of the customization option tier price.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the customization option tier price.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the customization option tier price.
     */
    private Date deletedAt;

}