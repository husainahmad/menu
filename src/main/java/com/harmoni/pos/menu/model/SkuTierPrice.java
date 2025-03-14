package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkuTierPrice {
    private Integer id;
    private Integer skuId;
    private Integer tierId;
    private Tier tier;
    private BigDecimal price;
    private Boolean deleted;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

}