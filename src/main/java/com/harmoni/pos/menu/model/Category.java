package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Entity representing a Category.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category {

    /**
     * The unique identifier of the category.
     */
    private Integer id;

    /**
     * The name of the category.
     */
    private String name;

    /**
     * The description of the category.
     */
    private String description;

    /**
     * The brand ID associated with the category.
     */
    private Integer brandId;

    /**
     * The brand entity associated with the category.
     */
    private Brand brand;

    /**
     * The creation timestamp of the category.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the category.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the category.
     */
    private Date deletedAt;

}