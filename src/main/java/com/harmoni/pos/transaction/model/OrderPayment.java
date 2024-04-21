package com.harmoni.pos.transaction.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderPayment {
    private Integer id;
    private Integer orderId;
    private Integer paymentId;
    private BigDecimal amount;
    private Date createdAt;
    private Date updatedAt;
}