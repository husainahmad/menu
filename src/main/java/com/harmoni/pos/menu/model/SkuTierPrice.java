package com.harmoni.pos.menu.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class SkuTierPrice {

    private Integer id;
    private Integer skuId;
    private Integer tierId;
    private Tier tier;
    private BigDecimal price;
    private Date createdAt;
    private Date updatedAt;

}