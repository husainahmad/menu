package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Entity representing the association between a Tier and a Category for menu purposes.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TierMenu {

    /**
     * The unique identifier of the tier menu association.
     */
    private Integer id;

    /**
     * The tier ID for this association.
     */
    private Integer tierId;

    /**
     * The Tier entity for this association.
     */
    private Tier tier;

    /**
     * The category ID for this association.
     */
    private Integer categoryId;

    /**
     * The Category entity for this association.
     */
    private Category category;

    /**
     * Indicates if the association is active.
     */
    private Boolean active;

    /**
     * Indicates if the association is deleted.
     */
    private Boolean deleted;

    /**
     * The creation timestamp of the association.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the association.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the association.
     */
    private Date deletedAt;

}