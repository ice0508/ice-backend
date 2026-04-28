package com.ou.service;

import com.ou.context.UserContext;
import com.ou.exception.BusinessException;
import com.ou.mapper.UserMapper;
import com.ou.pojo.DTO.LoginRequest;
import com.ou.pojo.DTO.LoginResponse;
import com.ou.pojo.PageResult;
import com.ou.pojo.Result;
import com.ou.pojo.UserMessage;
import com.ou.pojo.enums.UserIdentity;
import com.ou.pojo.vo.UserLoginVO;
import com.ou.utils.DesensitizationUtil;
import com.ou.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    private final LoginLogService loginLogService;
    private final HttpServletRequest request;
    private final JwtUtil jwtUtil;

    public Result getAllUserInfo() {
        List<UserMessage> userMessages = userMapper.getAllUserInfo();
        return Result.success(userMessages);
    }

    public Result addUserInfo(UserMessage user) {
        // 检查当前登录用户是否为管理员
        UserMessage currentUser = UserContext.getUser();
        if (currentUser == null || currentUser.getIdentity() != UserIdentity.ADMIN) {
            throw new BusinessException("操作者不是管理员!");
        }

        user.setPassword_hash(encoder.encode(user.getPassword_hash()));
        int count = userMapper.addUserInfo(user);
        if (count == 0) {
            return Result.serverError("添加失败");
        }
        return Result.success("添加成功");
    }

    public Result updateUserInfo(UserMessage user) {
        // 检查当前登录用户是否为管理员
        UserMessage currentUser = UserContext.getUser();
        if (currentUser == null || currentUser.getIdentity() != UserIdentity.ADMIN) {
            throw new BusinessException("操作者不是管理员!");
        }

        if (!StringUtils.hasText(user.getUsername())) {
            throw new BusinessException("用户名不能为空!");
        }

        // 检查用户名是否被他人使用
        UserMessage existingByUsername = userMapper.getUserById(user.getId());
        if (existingByUsername != null && !existingByUsername.getUsername().equals(user.getUsername())) {
            if (userMapper.countByUsername(user.getUsername()) > 0) {
                throw new BusinessException("该用户名已被注册!");
            }
        }

        // 检查邮箱是否被他人使用
        if (existingByUsername != null && !existingByUsername.getEmail().equals(user.getEmail())) {
            if (userMapper.countByEmail(user.getEmail()) > 0) {
                throw new BusinessException("该邮箱已被注册!");
            }
        }

        // 如果前端没传 status，保留原值
        if (user.getStatus() == null) {
            user.setStatus(existingByUsername.getStatus());
        }

        // 不更新密码和身份
        int count = userMapper.updateUserInfoWithoutIdentity(user);
        if (count == 0) {
            return Result.notFound("用户不存在!");
        }
        return Result.success("更新成功!");
    }

    public Result deleteUserById(Integer id) {
        // 检查当前登录用户是否为管理员
        UserMessage currentUser = UserContext.getUser();
        if (currentUser == null || currentUser.getIdentity() != UserIdentity.ADMIN) {
            throw new BusinessException("操作者不是管理员!");
        }

        int count = userMapper.deleteUserById(id);
        if (count == 0) {
            return Result.notFound("用户不存在!");
        }
        return Result.success("删除成功!");
    }

    // 用户注册
    public Result userRegister(UserMessage user) {
        if (!StringUtils.hasText(user.getUsername())) {
            throw new BusinessException("用户名不能为空!");
        }
        if (!StringUtils.hasText(user.getPassword_hash())) {
            throw new BusinessException("密码不能为空!");
        }

        // 唯一性校验（用户名、邮箱、手机号）
        if (userMapper.countByUsername(user.getUsername()) > 0) {
            throw new BusinessException("该用户名已被注册!");
        }
        if (userMapper.countByEmail(user.getEmail()) > 0) {
            throw new BusinessException("该邮箱已被注册!");
        }
        if (userMapper.countByPhone(user.getPhone()) > 0) {
            throw new BusinessException("该手机号已被注册!");
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

    // 用户登录
    public Result userLogin(LoginRequest req) {

        String account = req.getAccount();
        String password = req.getPassword();
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        UserMessage user = userMapper.findByAccount(req.getAccount());
        if (user == null) {
            // 记录失败日志：账号不存在
            loginLogService.recordLoginLog(null, account, ip, userAgent, false, "账号不存在");
            throw new BusinessException("该账号不存在!");
        }

        if (user.getStatus() != true) {
            loginLogService.recordLoginLog(user.getId(), account, ip, userAgent, false, "账号已禁用");
            throw new BusinessException("该账号已被禁用!");
        }

        // 验证密码
        if (!encoder.matches(req.getPassword(), user.getPassword_hash())) {
            loginLogService.recordLoginLog(user.getId(), account, ip, userAgent, false, "密码错误");
            throw new BusinessException("密码错误!");
        }

        UserLoginVO vo = new UserLoginVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(DesensitizationUtil.maskEmail(user.getEmail()));
        vo.setPhone(DesensitizationUtil.maskPhone(user.getPhone()));
        vo.setRole(user.getIdentity().getDescription());

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getIdentity().getDescription());

        // 返回 Token 和用户信息
        LoginResponse loginResponse = new LoginResponse(token, user.getUsername(), user.getIdentity().getDescription(), user.getId());

        loginLogService.recordLoginLog(user.getId(), account, ip, userAgent, true, null);
        return Result.success("登陆成功", loginResponse);
    }

    // 获取客户端真实 IP（考虑代理）
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果通过多层代理，取第一个非 unknown 的 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    // 分页设计
    public Result getUserPage(int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;

        List<UserMessage> users = userMapper.getUserByPage(offset, size);
        Long total = userMapper.countUsers();

        PageResult pageResult = new PageResult(total, users);
        return Result.success(pageResult);
    }
}
