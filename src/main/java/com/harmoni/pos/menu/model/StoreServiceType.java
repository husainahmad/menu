package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreServiceType {

    private Integer id;
    private Integer storeId;
    private Store store;
    private Integer subServiceId;
    private SubService subService;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

}