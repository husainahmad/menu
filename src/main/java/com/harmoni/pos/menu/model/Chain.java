package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity representing a Chain.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Chain {

    /**
     * The unique identifier of the chain.
     */
    private Integer id;

    /**
     * The name of the chain.
     */
    private String name;

    /**
     * The brand ID associated with the chain.
     */
    private Integer brandId;

    /**
     * The brand entity associated with the chain.
     */
    private Brand brand;

    /**
     * The chain type: {@code REGION}, {@code FRANCHISE} or {@code OTHER}.
     */
    private String type;

    /**
     * The email address of the chain manager.
     */
    private String managerEmail;

    /**
     * Whether tax/service settings are inherited from the brand
     * ({@code INHERIT}) or set on the chain ({@code OWN}).
     */
    private String taxServiceMode;

    /**
     * The tax percentage when {@link #taxServiceMode} is {@code OWN}.
     */
    private BigDecimal taxPercent;

    /**
     * The service-charge percentage when {@link #taxServiceMode} is {@code OWN}.
     */
    private BigDecimal serviceChargePercent;

    /**
     * The creation timestamp of the chain.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the chain.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the chain.
     */
    private Date deletedAt;

}