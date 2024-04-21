package com.harmoni.pos.transaction.model;

import lombok.Data;

import java.util.Date;

@Data
public class Order {
    private Integer id;
    private String orderNo;
    private Double subTotal;
    private Double discountTotal;
    private Double grandTotal;
    private Double taxTotal;
    private Integer storeId;
    private Integer storeServiceTypesId;
    private Date createdAt;
    private Date updatedAt;
}