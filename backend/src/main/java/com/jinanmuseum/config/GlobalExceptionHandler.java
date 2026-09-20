package com.jinanmuseum.config;

import com.jinanmuseum.common.BusinessException;
import com.jinanmuseum.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 业务异常：HTTP 400 + 中文 message（SPEC §5 约定，前端只展示 message） */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleBusinessException(BusinessException e) {
        return Result.error(e.getMessage());
    }

    /** 未预期异常：HTTP 500，不向前端泄漏内部细节，完整堆栈进日志 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleException(Exception e) {
        log.error("未处理异常", e);
        return Result.error("服务器开小差了，请稍后重试");
    }
}
