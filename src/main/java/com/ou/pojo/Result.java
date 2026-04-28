package com.ou.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result {
    private Integer code;      // 状态码（200=成功，400=客户端错误，500=服务器错误）
    private String message;    // 提示信息（成功时为"success"，失败时为具体原因）
    private Object data;       // 携带的数据（可以是对象、数组、null）

    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ========== 静态工厂方法：成功 ==========
    /** 成功，无数据 */
    public static Result success() {
        return new Result(200, "success", null);
    }

    /** 成功，带提示消息 */
    public static Result success(String message) {
        return new Result(200, message, null);
    }

    /** 成功，带数据 */
    public static Result success(Object data) {
        return new Result(200, "success", data);
    }

    /** 成功，带消息和数据 */
    public static Result success(String message, Object data) {
        return new Result(200, message, data);
    }

    // ========== 静态工厂方法：失败 ==========

    /** 失败，自定义状态码和消息 */
    public static Result error(Integer code, String message) {
        return new Result(code, message, null);
    }

    /** 失败，快捷定义（400 客户端错误） */
    public static Result badRequest(String message) {
        return new Result(400, message, null);
    }

    /** 失败，快捷定义（404 资源不存在） */
    public static Result notFound(String message) {
        return new Result(404, message, null);
    }

    /** 失败，快捷定义（500 服务器错误） */
    public static Result serverError(String message) {
        return new Result(500, message, null);
    }

    // ========== 链式设置 ==========
    public Result code(Integer code) {
        this.code = code;
        return this;
    }

    public Result message(String message) {
        this.message = message;
        return this;
    }

    public Result data(Object data) {
        this.data = data;
        return this;
    }
}