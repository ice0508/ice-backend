package com.ou.handler;

import com.ou.exception.BusinessException;
import com.ou.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@RestControllerAdvice   // @ControllerAdvice + @ResponseBody，返回 JSON
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class) // 方法参数无效异常 - 参数校验异常（@Valid 失败）
    @ResponseStatus(HttpStatus.BAD_REQUEST)   // 400
    public Result handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMsg = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("参数校验失败: {}", errorMsg);
        return Result.badRequest(errorMsg);
    }

    // 处理表单绑定异常（如 GET 请求参数类型不匹配）
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleBindException(BindException e) {
        FieldError fieldError = e.getFieldError();
        String errorMsg = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.warn("参数绑定失败: {}", errorMsg);
        return Result.badRequest(errorMsg);
    }

    // ========== 自定义业务异常 ==========
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    // ========== 数据库约束异常（如重复键、外键冲突） ==========
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("数据完整性异常: {}", e.getMessage());
        // 先尝试获取底层 SQL 异常
        Throwable cause = e.getCause();
        if (cause instanceof SQLIntegrityConstraintViolationException) {
            return handleSQLIntegrityConstraintViolation((SQLIntegrityConstraintViolationException) cause);
        }
        return Result.badRequest("数据操作冲突，请检查输入");
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleSQLIntegrityConstraintViolation(SQLIntegrityConstraintViolationException e) {
        log.error("数据库约束违反: {}", e.getMessage());
        String message = "操作失败，数据违反完整性约束";
        if (e.getMessage().contains("Duplicate entry")) {
            message = "该记录已存在，请勿重复添加";
        }
        return Result.badRequest(message);
    }

    // ========== 其他未捕获的异常（兜底） ==========
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result handleNoResourceFoundException(NoResourceFoundException e) {
        return Result.notFound("资源不存在");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   // 500
    public Result handleException(Exception e) {
        log.error("系统内部错误", e);   // 记录完整堆栈用于排查
        // 生产环境不应返回异常详情给前端
        return Result.serverError("服务器内部错误，请联系管理员");
    }
}