package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Represents a mapping between a product and its customization options.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCustomization {
    private Integer id;
    private Integer productId;
    private Integer customizationId;
    private Boolean isActive;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
