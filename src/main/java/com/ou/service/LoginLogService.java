package com.ou.service;

import com.ou.mapper.LoginLogMapper;
import com.ou.pojo.LoginLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginLogService {
    private final LoginLogMapper loginLogMapper;

    @Async   // 异步执行，不阻塞主线程
    public void recordLoginLog(Integer userId, String account, String ip, String userAgent,
                               boolean success, String failReason) {
        LoginLog logEntity = new LoginLog();
        logEntity.setUserId(userId);
        logEntity.setAccount(account);
        logEntity.setIpAddress(ip);
        logEntity.setUserAgent(userAgent);
        logEntity.setResult(success ? 1 : 0);
        logEntity.setFailReason(failReason);

        try {
            loginLogMapper.insert(logEntity);
        } catch (Exception e) {
            // 日志记录失败不应影响主业务，仅打印错误日志
            log.error("登录日志写入失败: {}", e.getMessage());
        }
    }
}