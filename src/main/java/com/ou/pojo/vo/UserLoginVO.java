package com.ou.pojo.vo;

import lombok.Data;

@Data
public class UserLoginVO {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String role;
}