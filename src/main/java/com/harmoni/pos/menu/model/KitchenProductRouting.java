package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Routes one product to a kitchen station. Takes priority over category routing.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KitchenProductRouting {

    private Integer id;
    private Integer storeId;
    private Integer productId;
    private Integer kitchenStationId;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    private String productName;
    private String kitchenStationName;
}
