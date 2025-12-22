package edu.example.library.controller;

import edu.example.library.dto.BorrowCreateRequest;
import edu.example.library.dto.BorrowDecisionRequest;
import edu.example.library.entity.BorrowRequest;
import edu.example.library.service.BorrowService;
import edu.example.library.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // 3.1 درخواست امانت (دانشجو)
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/request")
    public ApiResponse<BorrowRequest> create(@Valid @RequestBody BorrowCreateRequest req, HttpServletRequest http) {
        return ApiResponse.ok(borrowService.createRequest(req.getBookId()), http.getRequestURI());
    }

    // 3.2 لیست درخواست‌های در انتظار (کارمند)
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @GetMapping("/requests/pending")
    public ApiResponse<List<BorrowRequest>> pending(HttpServletRequest http) {
        return ApiResponse.ok(borrowService.pendingRequests(), http.getRequestURI());
    }

    // 3.3 تایید درخواست (کارمند)
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @PutMapping("/requests/{id}/approve")
    public ApiResponse<BorrowRequest> approve(@PathVariable Long id, HttpServletRequest http) {
        return ApiResponse.ok(borrowService.approve(id), http.getRequestURI());
    }

    // 3.3 رد درخواست (کارمند)
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @PutMapping("/requests/{id}/reject")
    public ApiResponse<BorrowRequest> reject(@PathVariable Long id,
                                             @Valid @RequestBody(required = false) BorrowDecisionRequest body,
                                             HttpServletRequest http) {
        String reason = body == null ? null : body.getReason();
        return ApiResponse.ok(borrowService.reject(id, reason), http.getRequestURI());
    }

    // 3.4 ثبت بازگرداندن (کارمند)
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @PutMapping("/{id}/return")
    public ApiResponse<BorrowRequest> returned(@PathVariable Long id, HttpServletRequest http) {
        return ApiResponse.ok(borrowService.markReturned(id), http.getRequestURI());
    }
}
