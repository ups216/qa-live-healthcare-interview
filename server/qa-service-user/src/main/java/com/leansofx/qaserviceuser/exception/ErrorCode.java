package com.leansofx.qaserviceuser.exception;

/**
 * 统一的业务错误码。
 *
 * <p>HTTP 状态码表达请求的处理类别，业务 code 表达具体原因，
 * 便于前端按 code 做分支，也让日志可检索。
 */
public enum ErrorCode {

    /** 通用：参数校验失败 */
    PARAM_INVALID(1002, org.springframework.http.HttpStatus.BAD_REQUEST),

    /** 需求2：查询的医生不存在 */
    DOCTOR_NOT_FOUND(1010, org.springframework.http.HttpStatus.NOT_FOUND),

    /** 需求3：患者注册 - 用户名已存在 */
    PATIENT_USERNAME_EXISTS(1001, org.springframework.http.HttpStatus.BAD_REQUEST),

    /** 需求3：患者登录 - 用户不存在（内部码，对外提示与密码错误一致，防止用户名枚举） */
    PATIENT_NOT_FOUND(1003, org.springframework.http.HttpStatus.UNAUTHORIZED),

    /** 需求3：患者登录 - 用户名或密码错误 */
    PATIENT_BAD_CREDENTIALS(1004, org.springframework.http.HttpStatus.UNAUTHORIZED),

    /** 需求3：缺少或未带 Bearer 前缀的 token */
    TOKEN_MISSING(1005, org.springframework.http.HttpStatus.UNAUTHORIZED),

    /** 需求3：token 无效或已过期 */
    TOKEN_INVALID(1006, org.springframework.http.HttpStatus.UNAUTHORIZED),

    /** 通用：系统内部错误 */
    SYSTEM_ERROR(5000, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final int code;
    private final org.springframework.http.HttpStatus httpStatus;

    ErrorCode(int code, org.springframework.http.HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public org.springframework.http.HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
