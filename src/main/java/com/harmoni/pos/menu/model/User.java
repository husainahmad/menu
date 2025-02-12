package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private Integer id;
    private Integer authId;
    private String username;

    private Integer storeId;
    private Store store;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
