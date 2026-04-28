package com.ou.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoginLog {
    private Long id;
    private Integer userId;           // 用户ID，登录失败时为 null
    private String account;        // 登录账号
    private LocalDateTime loginTime;
    private String ipAddress;
    private String userAgent;
    private Integer result;        // 1-成功，0-失败
    private String failReason;
}