package edu.example.library.exception;

import edu.example.library.util.ApiError;
import edu.example.library.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApi(ApiException ex, HttpServletRequest req) {
        ApiError err = new ApiError(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
                .body(ApiResponse.fail(err, req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, Object> details = new HashMap<>();
        Map<String, String> fields = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fields.put(fe.getField(), fe.getDefaultMessage());
        }
        details.put("fields", fields);
        ApiError err = new ApiError("VALIDATION_ERROR", "Validation failed", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(err, req.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccess(AccessDeniedException ex, HttpServletRequest req) {
        ApiError err = new ApiError("FORBIDDEN", "Access denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(err, req.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOther(Exception ex, HttpServletRequest req) {
        ApiError err = new ApiError("INTERNAL_ERROR", "Unexpected error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(err, req.getRequestURI()));
    }
}
