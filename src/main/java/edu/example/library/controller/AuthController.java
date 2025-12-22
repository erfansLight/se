package edu.example.library.controller;

import edu.example.library.dto.AuthResponse;
import edu.example.library.dto.ChangePasswordRequest;
import edu.example.library.dto.LoginRequest;
import edu.example.library.dto.RegisterStudentRequest;
import edu.example.library.entity.StudentProfile;
import edu.example.library.service.AuthService;
import edu.example.library.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 1.1 ثبت‌نام دانشجو
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterStudentRequest req,
                                                     HttpServletRequest http) {
        StudentProfile s = authService.registerStudent(req);
        Map<String, Object> data = Map.of(
                "studentId", s.getId(),
                "username", s.getUser().getUsername(),
                "fullName", s.getFullName(),
                "studentNumber", s.getStudentNumber(),
                "active", s.isActive()
        );
        return ApiResponse.ok(data, http.getRequestURI());
    }

    // 1.2 ورود (همه کاربران)
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest req, HttpServletRequest http) {
        return ApiResponse.ok(authService.login(req), http.getRequestURI());
    }

    // 1.3 تغییر رمزعبور (کارمند/مدیر)
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req, HttpServletRequest http) {
        authService.changePassword(req);
        return ApiResponse.ok(http.getRequestURI());
    }
}
