package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * Entity representing a Sku.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sku {

    /**
     * The unique identifier of the SKU.
     */
    private Integer id;

    /**
     * The name of the SKU.
     */
    private String name;

    /**
     * The product ID associated with the SKU.
     */
    private Integer productId;

    /**
     * The product entity associated with the SKU.
     */
    private Product product;

    /**
     * The creation timestamp of the SKU.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the SKU.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the SKU.
     */
    private Date deletedAt;

    /**
     * Indicates if the SKU is active.
     */
    private Boolean active;

    /**
     * Indicates if the SKU is deleted.
     */
    private Boolean deleted;

    /**
     * List of tier prices for this SKU.
     */
    @JsonProperty("tierPrices")
    private List<SkuTierPrice> skuTierPrices;

    /**
     * The tier price for this SKU.
     */
    @JsonProperty("tierPrice")
    private SkuTierPrice skuTierPrice;
}