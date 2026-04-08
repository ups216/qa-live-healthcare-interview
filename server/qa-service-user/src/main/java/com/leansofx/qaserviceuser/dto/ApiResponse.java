package com.leansofx.qaserviceuser.dto;

public class ApiResponse<T> {

    private T data;
    private int total;
    private String error;
    private String message;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setData(data);
        if (data instanceof java.util.List<?> list) {
            response.setTotal(list.size());
        }
        return response;
    }

    public static <T> ApiResponse<T> success(T data, int total) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setData(data);
        response.setTotal(total);
        return response;
    }

    public static <T> ApiResponse<T> error(String error, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setError(error);
        response.setMessage(message);
        return response;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
