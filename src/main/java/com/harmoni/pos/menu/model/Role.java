package com.harmoni.pos.menu.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class Role {

    private Integer id;
    private String name;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

}