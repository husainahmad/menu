package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Entity representing a Product Image.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductImage {

    /**
     * The unique identifier of the product image.
     */
    private Integer id;

    /**
     * The product ID associated with the image.
     */
    private Integer productId;

    /**
     * The file name of the product image.
     */
    private String fileName;

    /**
     * The hosted image URL as returned by ImgBB.
     */
    private String url;

    /**
     * The MIME type of the image.
     */
    private String mimeType;

    /**
     * The creation timestamp of the product image.
     */
    private Date createdAt;

    /**
     * The last update timestamp of the product image.
     */
    private Date updatedAt;

    /**
     * The deletion timestamp of the product image.
     */
    private Date deletedAt;

}