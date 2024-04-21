package com.harmoni.pos.menu.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class TierPrice {

    private Integer id;
    private Integer tierId;
    private Date createdAt;
    private Date updatedAt;

}