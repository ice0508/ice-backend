package com.ou.mapper;

import com.ou.pojo.UserMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM users")
    List<UserMessage> getAllUserInfo();

    @Insert("INSERT INTO users (username, password_hash, email, phone, status)" +
            " VALUES (#{username}, #{password_hash}, #{email}, #{phone}, #{status})")
    int addUserInfo(UserMessage userMessage);
}
