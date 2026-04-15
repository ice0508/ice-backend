package com.ou.pojo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserMessage {
    private Integer id;
    private String username;
    private String password_hash;
    private String email;
    private String phone;
    private boolean status = true;
    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at = LocalDateTime.now();

    public UserMessage() {}

    public UserMessage(Integer id, String username, String password_hash, String email, String phone, boolean status, LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.username = username;
        this.password_hash = password_hash;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
