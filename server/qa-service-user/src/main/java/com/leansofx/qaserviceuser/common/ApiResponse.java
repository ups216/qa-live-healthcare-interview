package com.leansofx.qaserviceuser.common;

/**
 * 统一 REST 响应封装。
 *
 * <p>替换原有 Controller 直接返回 {@code Map<String, Object>} 的做法，
 * 使前端可以用同一套拦截器剥壳处理，无需逐接口适配。
 *
 * @param <T> data 载荷类型
 */
public class ApiResponse<T> {

    /** 业务成功码 */
    public static final int CODE_OK = 0;

    private int code;
    private String message;
    private T data;
    private long timestamp;

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(CODE_OK, "success", data);
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(CODE_OK, message, data);
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
