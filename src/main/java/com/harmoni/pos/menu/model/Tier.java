package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * Entity representing a Tier, which can be associated with services and a brand.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tier {

    /**
     * The unique identifier of the tier.
     */
    private Integer id;

    /**
     * The name of the tier.
     */
    private String name;

    /**
     * The brand ID associated with the tier.
     */
    private Integer brandId;

    /**
     * The Brand entity associated with the tier.
     */
    private Brand brand;

    /**
     * The type of the tier.
     */
    private TierType type;

    /**
     * Indicates if the tier is deleted.
     */
    private Boolean deleted;

    /**
     * The creation timestamp of the tier.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the tier.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the tier.
     */
    private Date deletedAt;

    /**
     * List of services associated with the tier.
     */
    List<TierService> tierServices;

}