package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Store {

    private Integer id;
    private String name;
    private Integer chainId;
    private Chain chain;
    private String address;
    private String timeZone;

    private Integer tierMenuId;
    private Integer tierPriceId;
    private Integer tierServiceId;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

}