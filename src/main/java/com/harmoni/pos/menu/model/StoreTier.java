package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreTier {

    private Integer id;
    private Integer storeId;
    private Integer tierMenuId;
    private Integer tierServiceId;
    private Integer tierPriceId;

    private TierMenu tierMenu;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

}