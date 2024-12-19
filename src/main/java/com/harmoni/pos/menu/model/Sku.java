package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sku {

    private Integer id;
    private String name;
    private Integer productId;
    private Product product;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Boolean active;
    private Boolean deleted;
    private List<SkuTierPrice> skuTierPrices;

}