package edu.example.library.controller;

import edu.example.library.dto.EmployeeCreateRequest;
import edu.example.library.entity.EmployeeProfile;
import edu.example.library.service.AdminEmployeeService;
import edu.example.library.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employees")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEmployeeController {

    private final AdminEmployeeService adminEmployeeService;

    public AdminEmployeeController(AdminEmployeeService adminEmployeeService) {
        this.adminEmployeeService = adminEmployeeService;
    }

    // 6.1 ایجاد کارمند (مدیر)
    @PostMapping
    public ApiResponse<EmployeeProfile> create(@Valid @RequestBody EmployeeCreateRequest req,
                                               HttpServletRequest http) {
        return ApiResponse.ok(adminEmployeeService.createEmployee(req), http.getRequestURI());
    }

    // 6.2 لیست کارکنان (مدیر)
    @GetMapping
    public ApiResponse<List<EmployeeProfile>> list(HttpServletRequest http) {
        return ApiResponse.ok(adminEmployeeService.listEmployees(), http.getRequestURI());
    }
}
