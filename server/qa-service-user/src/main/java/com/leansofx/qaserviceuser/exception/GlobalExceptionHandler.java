package com.leansofx.qaserviceuser.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 全局 API 异常处理，保证接口返回一致的 JSON 错误体，便于前端与测试脚本识别。
 * 避免未捕获异常导致 Spring 默认 HTML 错误页或空响应。
 */
@RestControllerAdvice(basePackages = "com.leansofx.qaserviceuser.controller")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.warn("API 未处理异常: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .body(Map.of(
                        "error", "INTERNAL_ERROR",
                        "message", "服务暂时不可用，请稍后重试"
                ));
    }
}
