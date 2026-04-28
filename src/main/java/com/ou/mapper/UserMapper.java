package com.ou.mapper;

import com.ou.pojo.UserMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    // 查询所有用户
    @Select("SELECT * FROM users")
    List<UserMessage> getAllUserInfo();

    // 根据账号(用户名或邮箱)查询用户(登录用)
    @Select("SELECT * FROM users WHERE email = #{account} OR username = #{account}")
    UserMessage findByAccount(String account);

    // 分页查询用户列表
    @Select("SELECT * FROM users LIMIT #{offset}, #{size}")
    List<UserMessage> getUserByPage(@Param("offset") int offset, @Param("size") int size);

    // 统计用户总数
    @Select("SELECT COUNT(*) FROM users")
    Long countUsers();

    // 根据ID查询用户（用于Token验证）
    @Select("SELECT * FROM users WHERE id = #{id}")
    UserMessage getUserById(Integer id);

    // ====================   存在性方法检验   ====================
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(String email);

    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username);

    @Select("SELECT COUNT(*) FROM users WHERE phone = #{phone}")
    int countByPhone(String phone);

    // 新增用户
    @Insert("INSERT INTO users (username, password_hash, email, phone, status, identity)" +
            " VALUES (#{username}, #{password_hash}, #{email}, #{phone}, #{status}, #{identity})")
    int addUserInfo(UserMessage userMessage);

    // 不更新密码和身份
    @Update("UPDATE users SET username = #{username}, email = #{email}, " +
            "phone = #{phone}, status = #{status} WHERE id = #{id}")
    int updateUserInfoWithoutIdentity(UserMessage userMessage);

    // 通过id删除用户
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteUserById(Integer id);

}
