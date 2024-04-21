package com.harmoni.pos.menu.model;

import io.github.weasleyj.mybatis.encrypt.annotation.Encryption;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class User {

    private Integer id;
    private String email;
    private String name;
    @Encryption
    private String password;

    private Integer storeId;
    private Integer roleId;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

}