package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Entity representing a Tier, which can be associated with services and a brand.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreTier {

    private Integer id;
    /**
     * The unique identifier of the tier.
     */
    private Integer storeId;

    /**
     * The name of the tier.
     */
    private Integer tierMenuId;

    /**
     * The brand ID associated with the tier.
     */
    private Integer tierServiceId;

    /**
     * The Brand entity associated with the tier.
     */
    private Integer tierPriceId;

    /**
     * The type of the tier.
     */


    /**
     * Indicates if the tier is deleted.
     */
    private TierMenu tierMenu;

    /**
     * The creation timestamp of the tier.
     */


    /**
     * The last update timestamp of the tier.
     */
    private Date createdAt;

    /**
     * The deletion timestamp of the tier.
     */
    private Date updatedAt;
    private Date deletedAt;
    /**
     * List of services associated with the tier.
     */

}