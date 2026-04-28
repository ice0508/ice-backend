package com.ou.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "账号不能为空！")
    private String account;

    @NotBlank(message = "密码不能为空！")
    @Size(min = 3, max = 20, message = "密码长度必须为3~20位")
    private String password;
}
