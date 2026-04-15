package com.ou.service;

import com.ou.mapper.UserMapper;
import com.ou.pojo.Result;
import com.ou.pojo.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    public Result getAllUserInfo() {
        List<UserMessage> userMessages = userMapper.getAllUserInfo();
        return new Result(200L, userMessages);
    }

    public Result addUserInfo(UserMessage addUserMessage) {
        int count = userMapper.addUserInfo(addUserMessage);
        if (count == 0) {
            return new Result(500L, "添加失败");
        }
        return new Result(200L, "添加成功");
    }
}
