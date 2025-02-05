package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductImage {

    private Integer id;
    private Integer productId;
    private String fileName;
    private byte[] imageBlob;
    private String mimeType;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

}