package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Entity representing the association between a Tier and a SubService.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TierService {

    /**
     * The unique identifier of the tier service.
     */
    private Integer id;

    /**
     * The tier ID associated with this service.
     */
    private Integer tierId;

    /**
     * The Tier entity associated with this service.
     */
    private Tier tier;

    /**
     * The sub-service ID associated with this tier service.
     */
    private Integer subServiceId;

    /**
     * The SubService entity associated with this tier service.
     */
    private SubService subService;

    /**
     * Indicates if the tier service is active.
     */
    private Boolean active;

    /**
     * Indicates if the tier service is deleted.
     */
    private Boolean deleted;

    /**
     * The creation timestamp of the tier service.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the tier service.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the tier service.
     */
    private Date deletedAt;

}