package com.ou.controller;

import com.ou.pojo.DTO.LoginRequest;
import com.ou.pojo.Result;
import com.ou.pojo.UserMessage;
import com.ou.pojo.enums.UserIdentity;
import com.ou.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    // 查询所有用户
    @GetMapping
    public Result getAllUserInfo() {
        return userService.getAllUserInfo();
    }

    // 分页
    @GetMapping("/page")
    public Result getUserPage(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size) {
        return userService.getUserPage(page, size);
    }

    // 新增用户
    @PostMapping
    public Result addUserInfo(@RequestBody UserMessage userMessage) {
        return userService.addUserInfo(userMessage);
    }

    // 更新用户（ID 从路径获取，更新内容从请求体获取）
    @PutMapping("/{id}")
    public Result updateUserInfo(@PathVariable Integer id,
                                 @RequestBody UserMessage userMessage) {
        userMessage.setId(id);
        return userService.updateUserInfo(userMessage);
    }

    // 删除用户（ID 从路径获取）
    @DeleteMapping("/{id}")
    public Result deleteUserById(@PathVariable Integer id) {
        return userService.deleteUserById(id);
    }

    // 用户注册
    @PostMapping("/register")
    public Result userRegister(@RequestBody UserMessage user) {
        user.setIdentity(UserIdentity.USER);
        return userService.userRegister(user);
    }

    // 用户登录
    @PostMapping("/login")
    public Result userLogin(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.userLogin(loginRequest);
    }
}