package com.ou.mapper;

import com.ou.pojo.LoginLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogMapper {
    @Insert("INSERT INTO login_logs (user_id, account, ip_address, user_agent, result, fail_reason) " +
            "VALUES (#{userId}, #{account}, #{ipAddress}, #{userAgent}, #{result}, #{failReason})")
    int insert(LoginLog log);
}