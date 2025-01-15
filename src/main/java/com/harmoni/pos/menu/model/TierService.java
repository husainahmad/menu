package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TierService {

    private Integer id;
    private Integer tierId;
    private Tier tier;

    private Integer subServiceId;
    private SubService subService;

    private Boolean active;
    private Boolean deleted;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

}