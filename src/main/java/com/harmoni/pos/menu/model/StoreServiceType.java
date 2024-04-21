package com.harmoni.pos.menu.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class StoreServiceType {

    private Integer id;
    private Integer storeId;
    private Integer subServiceId;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

}