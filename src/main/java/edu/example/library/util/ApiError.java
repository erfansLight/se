package edu.example.library.util;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

public class ApiError {
    private String code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> details;

    public ApiError() {
    }

    public ApiError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiError(String code, String message, Map<String, Object> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
