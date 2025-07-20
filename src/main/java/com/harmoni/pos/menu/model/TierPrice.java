package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Entity representing the price configuration for a Tier.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TierPrice {

    /**
     * The unique identifier of the tier price.
     */
    private Integer id;

    /**
     * The tier ID associated with this price.
     */
    private Integer tierId;

    /**
     * Indicates if the tier price is deleted.
     */
    private Boolean deleted;

    /**
     * The creation timestamp of the tier price.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the tier price.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the tier price.
     */
    private Date deletedAt;

}