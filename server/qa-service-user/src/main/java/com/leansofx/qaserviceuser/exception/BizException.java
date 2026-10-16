package com.leansofx.qaserviceuser.exception;

import org.springframework.http.HttpStatus;

/**
 * 业务异常：携带业务错误码与对应 HTTP 状态，由 {@link GlobalExceptionHandler} 统一转换为 ApiResponse。
 */
public class BizException extends RuntimeException {

    private final int code;
    private final HttpStatus httpStatus;

    public BizException(ErrorCode errorCode) {
        this(errorCode, errorCode.name());
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
    }

    public BizException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
