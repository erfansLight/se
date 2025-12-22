package edu.example.library.util;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

public class ApiResponse<T> {

    private boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ApiError error;

    private Instant timestamp = Instant.now();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String path;

    public static <T> ApiResponse<T> ok(T data, String path) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.data = data;
        r.path = path;
        return r;
    }

    public static ApiResponse<Void> ok(String path) {
        ApiResponse<Void> r = new ApiResponse<>();
        r.success = true;
        r.path = path;
        return r;
    }

    public static ApiResponse<Void> fail(ApiError error, String path) {
        ApiResponse<Void> r = new ApiResponse<>();
        r.success = false;
        r.error = error;
        r.path = path;
        return r;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public ApiError getError() {
        return error;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }
}
