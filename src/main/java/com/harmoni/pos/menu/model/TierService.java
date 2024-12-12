package com.harmoni.pos.menu.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class TierService {

    private Integer id;
    private Integer tierId;
    private Tier tier;

    private Integer subServiceId;
    private boolean active;
    private Date createdAt;
    private Date updatedAt;

}