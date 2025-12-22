package edu.example.library.controller;

import edu.example.library.config.UserPrincipal;
import edu.example.library.dto.StudentStatusUpdateRequest;
import edu.example.library.entity.BorrowRequest;
import edu.example.library.entity.Role;
import edu.example.library.entity.StudentProfile;
import edu.example.library.exception.ApiException;
import edu.example.library.repo.StudentRepository;
import edu.example.library.service.BorrowService;
import edu.example.library.service.StudentService;
import edu.example.library.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final BorrowService borrowService;
    private final StudentRepository studentRepo;

    public StudentController(StudentService studentService, BorrowService borrowService, StudentRepository studentRepo) {
        this.studentService = studentService;
        this.borrowService = borrowService;
        this.studentRepo = studentRepo;
    }

    private void ensureSelfOrStaff(Long studentId) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a != null && a.getPrincipal() instanceof UserPrincipal p) {
            var role = p.getUser().getRole();
            if (role == Role.STUDENT) {
                Long currentStudentId = studentRepo.findByUserId(p.getUser().getId())
                        .map(StudentProfile::getId)
                        .orElse(null);
                if (currentStudentId == null || !currentStudentId.equals(studentId)) {
                    throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "You can only access your own profile");
                }
            }
        }
    }

    // 4.1 پروفایل دانشجو
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ApiResponse<StudentProfile> get(@PathVariable Long id, HttpServletRequest http) {
        ensureSelfOrStaff(id);
        return ApiResponse.ok(studentService.get(id), http.getRequestURI());
    }

    // 4.2 فعال/غیرفعال کردن (کارمند)
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @PutMapping("/{id}/status")
    public ApiResponse<StudentProfile> status(@PathVariable Long id,
                                              @Valid @RequestBody StudentStatusUpdateRequest req,
                                              HttpServletRequest http) {
        return ApiResponse.ok(studentService.setActive(id, req.getActive()), http.getRequestURI());
    }

    // 4.3 تاریخچه امانت‌های دانشجو (کارمند)
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @GetMapping("/{id}/borrow-history")
    public ApiResponse<List<BorrowRequest>> history(@PathVariable Long id, HttpServletRequest http) {
        return ApiResponse.ok(borrowService.historyForStudent(id), http.getRequestURI());
    }
}
