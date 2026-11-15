package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Routes every product in a category to one kitchen station, unless a product
 * level routing overrides it.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KitchenCategoryRouting {

    private Integer id;
    private Integer storeId;
    private Integer categoryId;
    private Integer kitchenStationId;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    private String categoryName;
    private String kitchenStationName;
}
