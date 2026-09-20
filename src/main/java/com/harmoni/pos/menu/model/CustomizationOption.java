package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * Represents an option under a specific customization.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomizationOption {
    /**
     * The unique identifier of the customization option.
     */
    private Integer id;

    /**
     * The customization ID this option belongs to.
     */
    private Integer customizationId;

    /**
     * The name of the option.
     */
    private String name;

    /**
     * Tier-based prices for this option.
     */
    private List<CustomizationOptionTierPrice> tierPrices;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Boolean isDeleted;
}
