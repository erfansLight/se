package edu.example.library.controller;

import edu.example.library.entity.BorrowRequest;
import edu.example.library.service.StatsService;
import edu.example.library.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    // 5.1 آمار خلاصه (مهمان/کارمند/مدیر)
    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> summary(HttpServletRequest http) {
        return ApiResponse.ok(statsService.summary(), http.getRequestURI());
    }

    // 5.2 آمار پیشرفته امانت‌ها (مدیر)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/borrows")
    public ApiResponse<List<BorrowRequest>> borrows(@RequestParam(required = false) String status,
                                                    HttpServletRequest http) {
        return ApiResponse.ok(statsService.borrows(status), http.getRequestURI());
    }

    // 5.3 گزارش عملکرد کارمند (مدیر)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/employees/{id}/performance")
    public ApiResponse<Map<String, Object>> employeePerf(@PathVariable Long id, HttpServletRequest http) {
        return ApiResponse.ok(statsService.employeePerformance(id), http.getRequestURI());
    }

    // 5.4 دانشجویان با بیشترین تاخیر (مدیر)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/top-delayed")
    public ApiResponse<List<Map<String, Object>>> topDelayed(@RequestParam(defaultValue = "10") int limit,
                                                             HttpServletRequest http) {
        return ApiResponse.ok(statsService.topDelayed(limit), http.getRequestURI());
    }
}
