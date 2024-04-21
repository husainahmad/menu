package com.harmoni.pos.transaction.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderDetail {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private Integer skuId;
    private String skuName;
    private Double quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private Date createdAt;
    private Date updatedAt;
}