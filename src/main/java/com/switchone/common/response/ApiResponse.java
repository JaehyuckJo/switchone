package com.switchone.common.response;

public record ApiResponse<T>(String code, String message, T returnObject) {

    public static <T> ApiResponse<T> ok(T returnObject) {
        return new ApiResponse<>("OK", "SUCCESS", returnObject);
    }
}
