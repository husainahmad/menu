package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Entity representing a Brand.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Brand {

    /**
     * The unique identifier of the brand.
     */
    private Integer id;

    /**
     * The name of the brand.
     */
    private String name;

    /**
     * The creation timestamp of the brand.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the brand.
     */
    private Date updatedAt;

}