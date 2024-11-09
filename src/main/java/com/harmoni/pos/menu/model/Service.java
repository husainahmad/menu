package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Service {

    private Integer id;
    private String name;
    List<SubService> subServices;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

}