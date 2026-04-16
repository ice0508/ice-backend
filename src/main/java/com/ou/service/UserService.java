package com.ou.service;

import com.ou.mapper.UserMapper;
import com.ou.pojo.DTO.LoginRequest;
import com.ou.pojo.Result;
import com.ou.pojo.UserMessage;
import com.ou.pojo.enums.UserIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Result getAllUserInfo() {
        List<UserMessage> userMessages = userMapper.getAllUserInfo();
        return Result.success(userMessages);
    }

    public Result addUserInfo(UserMessage user) {
        user.setPassword_hash(encoder.encode(user.getPassword_hash()));
        int count = userMapper.addUserInfo(user);
        if (count == 0) {
            return Result.serverError("添加失败");
        }
        return Result.success("添加成功");
    }

    public Result updateUserInfo(UserMessage user) {
        if (user.getIdentity() != UserIdentity.ADMIN) {
            return Result.badRequest("操作者不是管理员!");
        }

        if (!StringUtils.hasText(user.getUsername())) {
            return Result.badRequest("用户名不能为空!");
        }

        if (userMapper.countByUsername(user.getUsername()) > 0) {
            return Result.badRequest("该用户名已被注册!");
        }
        if (userMapper.countByEmail(user.getEmail()) > 0) {
            return Result.badRequest("该邮箱已被注册!");
        }
        if (userMapper.countByPhone(user.getPhone()) > 0) {
            return Result.badRequest("该手机号已被注册!");
        }


        int count = userMapper.updateUserInfo(user);
        if (count == 0) {
            return Result.notFound("用户不存在!");
        }
        return Result.success("更新成功!");
    }

    public Result deleteUserById(Integer id) {
        int count = userMapper.deleteUserById(id);
        if (count == 0) {
            return Result.notFound("用户不存在!");
        }
        return Result.success("删除成功!");
    }

    public Result userRegister(UserMessage user) {
        if (!StringUtils.hasText(user.getUsername())) {
            return Result.badRequest("用户名不能为空!");
        }
        if (!StringUtils.hasText(user.getPassword_hash())) {
            return Result.badRequest("密码不能为空!");
        }

        // 唯一性校验（用户名、邮箱、手机号）
        if (userMapper.countByUsername(user.getUsername()) > 0) {
            return Result.badRequest("该用户名已被注册!");
        }
        if (userMapper.countByEmail(user.getEmail()) > 0) {
            return Result.badRequest("该邮箱已被注册!");
        }
        if (userMapper.countByPhone(user.getPhone()) > 0) {
            return Result.badRequest("该手机号已被注册!");
        }

        user.setPassword_hash(encoder.encode(user.getPassword_hash()));

        // 默认为"普通用户", 状态为true
        user.setIdentity(UserIdentity.USER);
        if (user.getStatus() == null) {
            user.setStatus(true);
        }

        int count = userMapper.addUserInfo(user);
        if (count == 0) {
            return Result.error(500, "注册失败，请稍后重试!");
        }
        return Result.success("注册成功!");
    }

    public Result userLogin(LoginRequest req) {

        UserMessage user = userMapper.findByAccount(req.getAccount());
        if (user == null) {
            return Result.badRequest("该账号不存在!");
        }

        if (user.getStatus() != true) {
            return Result.badRequest("该账号已被禁用!");
        }

        // 验证密码
        if (!encoder.matches(req.getPassword(), user.getPassword_hash())) {
            return Result.badRequest("密码错误!");
        }
        user.setPassword_hash(null);
        return Result.success("登陆成功", user);
    }
}
