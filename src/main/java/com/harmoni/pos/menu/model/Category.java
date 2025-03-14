package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category {

    private Integer id;
    private String name;
    private String description;
    private Integer brandId;
    private Brand brand;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

}