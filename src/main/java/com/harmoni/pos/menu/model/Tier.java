package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tier {

    private Integer id;
    private String name;
    private Integer brandId;
    private Brand brand;
    private TierType type;
    private Date createdAt;
    private Date updatedAt;

}