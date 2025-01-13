package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubService {

    private Integer id;
    private Integer serviceId;
    private String name;
    private Service service;
    private Date createdAt;
    private Date updatedAt;

}