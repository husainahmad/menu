package com.harmoni.pos.menu.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class SubService {

    private Integer id;
    private Integer serviceId;
    private String name;
    private Date createdAt;
    private Date updatedAt;

}