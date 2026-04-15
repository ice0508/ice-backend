package com.ou.controller;

import com.ou.pojo.Result;
import com.ou.pojo.UserMessage;
import com.ou.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    @GetMapping("/getAllUserInfo")
    public Result getAllUserInfo() {return userService.getAllUserInfo();}

    @PostMapping("/addUserInfo")
    public Result addUserInfo(@RequestBody UserMessage userMessage) {
        return userService.addUserInfo(userMessage);
    }
}
