package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * A kitchen station belonging to a store. Products are routed to stations either
 * directly or through their category.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KitchenStation {

    private Integer id;
    private Integer storeId;
    private String name;
    private String code;
    private String description;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    /**
     * Printers configured for the station. Populated by the detail query only.
     */
    private List<KitchenPrinter> printers;
}
