package com.ou.pojo.DTO;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private Integer id;

    public LoginResponse(String token, String username, String role, Integer id) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.id = id;
    }
}