# JWT 功能介绍

## 概述

JWT（JSON Web Token）是一种开放标准（RFC 7519），用于在各方之间安全地传输信息。本项目使用 JWT 实现用户身份认证和授权管理。

---

## 一、JWT 配置（application.yaml）

```yaml
jwt:
  secret: "ou-secret-key-for-jwt-token-generation-2024-must-be-at-least-32-bytes"
  expiration: 86400000          # 24小时（毫秒）
```

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| secret | JWT 加密密钥（至少32字节） | ou-secret-key-for-jwt-token-generation-2024 |
| expiration | Token 有效期（毫秒） | 86400000（24小时） |

---

## 二、核心组件

### 1. JwtUtil（JWT 工具类）

**文件位置**：`src/main/java/com/ou/utils/JwtUtil.java`

**功能**：

| 方法 | 说明 |
|------|------|
| `generateToken(userId, username, role)` | 生成 Token，包含用户ID、用户名、身份信息 |
| `parseToken(token)` | 解析 Token，返回 Claims 对象 |
| `getUserIdFromToken(token)` | 从 Token 中提取用户ID |
| `getUsernameFromToken(token)` | 从 Token 中提取用户名 |
| `getRoleFromToken(token)` | 从 Token 中提取用户身份/角色 |
| `validateToken(token)` | 验证 Token 是否有效（签名+过期） |

**Token 结构**：
```
- subject（主题）：用户ID
- claim: username（用户名）
- claim: role（身份角色）
- issuedAt：签发时间
- expiration：过期时间
```

---

### 2. JwtInterceptor（JWT 拦截器）

**文件位置**：`src/main/java/com/ou/interceptor/JwtInterceptor.java`

**功能**：
- 拦截所有 HTTP 请求（除白名单路径外）
- 从请求头 `Authorization` 中提取 Token
- 验证 Token 有效性
- 将用户信息存入 `UserContext`（ThreadLocal）

**白名单路径**（无需认证）：
```java
- /users/login（登录）
- /users/register（注册）
- /admin.html（管理员页面）
- /login.html（登录页面）
```

**静态资源放行**：`.html`、`.css`、`.js`、(`/static/`、`.favicon`)

---

### 3. WebConfig（Web 配置）

**文件位置**：`src/main/java/com/ou/config/WebConfig.java`

**功能**：
- 注册 `JwtInterceptor` 拦截器，拦截所有路径
- 配置 CORS 跨域资源共享

---

## 三、使用流程

### 1. 用户登录 → 生成 Token

```
用户 POST /users/login
    ↓
UserService.userLogin() 验证账号密码
    ↓
JwtUtil.generateToken() 生成 Token
    ↓
返回 LoginResponse { token, username, role, id }
```

**LoginResponse 结构**：
```java
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "张三",
    "role": "普通用户",
    "id": 1
}
```

### 2. 请求认证 → 验证 Token

```
客户端请求（带 Header: Authorization: Bearer <token>）
    ↓
JwtInterceptor.preHandle() 拦截
    ↓
验证 Token 签名和有效期
    ↓
解析 Token 获取 userId
    ↓
数据库查询用户信息
    ↓
存入 UserContext（ThreadLocal）
    ↓
请求继续处理
    ↓
afterCompletion() 清理 ThreadLocal
```

---

## 四、关键特性

| 特性 | 说明 |
|------|------|
| **HMAC-SHA256 签名** | 使用 HMAC-SHA256 算法签名，防篡改 |
| **自包含** | Token 包含用户信息，无需每次查询数据库 |
| **无状态** | 服务端不存储 Session，支持分布式部署 |
| **ThreadLocal 存储** | 用户信息存储在 ThreadLocal，请求结束后清理 |
| **登录日志** | 记录登录成功/失败及原因、IP、User-Agent |

---

## 五、安全措施

1. **密码加密**：使用 BCrypt 加密存储
2. **Token 时效**：默认24小时过期
3. **用户状态检查**：验证用户是否被禁用
4. **ThreadLocal 清理**：请求完成后防止内存泄漏
5. **跨域配置**：支持跨域请求

---

## 六、相关文件清单

| 文件 | 路径 | 作用 |
|------|------|------|
| JwtUtil.java | `src/main/java/com/ou/utils/` | JWT 工具类 |
| JwtInterceptor.java | `src/main/java/com/ou/interceptor/` | JWT 拦截器 |
| WebConfig.java | `src/main/java/com/ou/config/` | Web 配置（注册拦截器） |
| UserService.java | `src/main/java/com/ou/service/` | 用户服务（登录时生成 Token） |
| LoginResponse.java | `src/main/java/com/ou/pojo/DTO/` | 登录响应 DTO |
| UserContext.java | `src/main/java/com/ou/context/` | 用户上下文（ThreadLocal） |
| application.yaml | `src/main/resources/` | JWT 配置 |
