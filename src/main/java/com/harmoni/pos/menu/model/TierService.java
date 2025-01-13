package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TierService {

    private Integer id;
    private Integer tierId;
    private Tier tier;

    private Integer subServiceId;
    private SubService subService;

    private boolean active;
    private Date createdAt;
    private Date updatedAt;

}