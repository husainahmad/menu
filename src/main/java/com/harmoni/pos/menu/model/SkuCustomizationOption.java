package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Represents a mapping between SKU and customization options.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkuCustomizationOption {
    private Integer id;
    private Integer skuId;
    private Integer customizationOptionId;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;
}
