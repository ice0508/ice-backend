package com.ou.interceptor;

import com.ou.context.UserContext;
import com.ou.mapper.UserMapper;
import com.ou.pojo.UserMessage;
import com.ou.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    // 不需要验证 Token 的路径
    private static final String[] EXCLUDE_PATHS = {
            "/users/login",
            "/users/register",
            "/admin.html",
            "/login.html"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行静态资源
        String uri = request.getRequestURI();
        if (uri.startsWith("/static/") ||
            uri.startsWith("/favicon") ||
            uri.endsWith(".html") ||
            uri.endsWith(".css") ||
            uri.endsWith(".js")) {
            return true;
        }

        // 检查是否在排除路径中
        for (String excludePath : EXCLUDE_PATHS) {
            if (uri.equals(excludePath)) {
                return true;
            }
        }

        // 获取 Token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\"}");
            return false;
        }

        // 去掉 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token 无效或已过期\"}");
            return false;
        }

        // 解析 Token 获取用户信息
        Claims claims = jwtUtil.parseToken(token);
        Integer userId = Integer.valueOf(claims.getSubject());

        // 从数据库查询完整用户信息
        UserMessage user = userMapper.getUserById(userId);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"用户不存在\"}");
            return false;
        }

        // 将用户信息存入 ThreadLocal
        UserContext.setUser(user);
        log.debug("用户 {} 已登录，身份：{}", user.getUsername(), user.getIdentity().getDescription());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束后清理 ThreadLocal，防止内存泄漏
        UserContext.removeUser();
    }
}
