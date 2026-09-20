package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * Entity representing a Product.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {

    /**
     * The unique identifier of the product.
     */
    private Integer id;

    /**
     * The name of the product.
     */
    private String name;

    /**
     * The category ID associated with the product.
     */
    private Integer categoryId;

    /**
     * The description of the product.
     */
    private String description;

    /**
     * The category entity associated with the product.
     */
    private Category category;

    /**
     * The list of customizations assigned to the product.
     */
    @JsonProperty("customizations")
    private List<Customization> customizations;

    /**
     * The list of SKUs for the product.
     */
    @JsonProperty("skus")
    private List<Sku> skus;

    /**
     * The product image entity.
     */
    private ProductImage productImage;

    /**
     * Indicates if the product is deleted.
     */
    private Boolean deleted;

    /**
     * The creation timestamp of the product.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the product.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the product.
     */
    private Date deletedAt;

}