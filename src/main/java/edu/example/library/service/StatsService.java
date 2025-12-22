package edu.example.library.service;

import edu.example.library.entity.BorrowRequest;
import edu.example.library.entity.BorrowStatus;
import edu.example.library.entity.StudentProfile;
import edu.example.library.repo.BookRepository;
import edu.example.library.repo.BorrowRequestRepository;
import edu.example.library.repo.EmployeeRepository;
import edu.example.library.repo.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final StudentRepository studentRepo;
    private final BookRepository bookRepo;
    private final BorrowRequestRepository borrowRepo;
    private final EmployeeRepository employeeRepo;

    public StatsService(StudentRepository studentRepo,
                        BookRepository bookRepo,
                        BorrowRequestRepository borrowRepo,
                        EmployeeRepository employeeRepo) {
        this.studentRepo = studentRepo;
        this.bookRepo = bookRepo;
        this.borrowRepo = borrowRepo;
        this.employeeRepo = employeeRepo;
    }

    public Map<String, Object> summary() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("students", studentRepo.count());
        m.put("books", bookRepo.count());
        m.put("borrows", borrowRepo.count());
        m.put("pendingBorrows", borrowRepo.countByStatus(BorrowStatus.PENDING));
        m.put("approvedBorrows", borrowRepo.countByStatus(BorrowStatus.APPROVED));
        m.put("returnedBorrows", borrowRepo.countByStatus(BorrowStatus.RETURNED));
        return m;
    }

    public List<BorrowRequest> borrows(String status) {
        if (status == null || status.isBlank()) {
            return borrowRepo.findAll();
        }
        return borrowRepo.findByStatus(BorrowStatus.valueOf(status.toUpperCase()));
    }

    public Map<String, Object> employeePerformance(Long employeeId) {
        // Performance is measured by number of approvals + returns.
        long approvals = borrowRepo.findAll().stream()
                .filter(b -> b.getApprovedBy() != null && Objects.equals(b.getApprovedBy().getId(), employeeId))
                .count();
        long returns = borrowRepo.findAll().stream()
                .filter(b -> b.getReturnedBy() != null && Objects.equals(b.getReturnedBy().getId(), employeeId))
                .count();
        long rejects = borrowRepo.findAll().stream()
                .filter(b -> b.getRejectedBy() != null && Objects.equals(b.getRejectedBy().getId(), employeeId))
                .count();

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("employeeUserId", employeeId);
        m.put("approvals", approvals);
        m.put("returns", returns);
        m.put("rejects", rejects);
        m.put("totalActions", approvals + returns + rejects);
        return m;
    }

    public List<Map<String, Object>> topDelayed(int limit) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        List<BorrowRequest> all = borrowRepo.findAll();

        // compute late days per student
        Map<Long, Long> lateDaysByStudent = new HashMap<>();
        for (BorrowRequest b : all) {
            if (b.getDueDate() == null) continue;

            LocalDate due = b.getDueDate();
            LocalDate end;

            if (b.getStatus() == BorrowStatus.RETURNED && b.getReturnedAt() != null) {
                end = b.getReturnedAt().atZone(ZoneOffset.UTC).toLocalDate();
            } else if (b.getStatus() == BorrowStatus.APPROVED) {
                end = today;
            } else {
                continue;
            }

            if (end.isAfter(due)) {
                long daysLate = java.time.temporal.ChronoUnit.DAYS.between(due, end);
                Long sid = b.getStudent().getId();
                lateDaysByStudent.put(sid, lateDaysByStudent.getOrDefault(sid, 0L) + daysLate);
            }
        }

        Map<Long, StudentProfile> studentMap = studentRepo.findAllById(lateDaysByStudent.keySet())
                .stream().collect(Collectors.toMap(StudentProfile::getId, s -> s));

        return lateDaysByStudent.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(Math.max(1, limit))
                .map(e -> {
                    StudentProfile s = studentMap.get(e.getKey());
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("studentId", e.getKey());
                    row.put("studentNumber", s != null ? s.getStudentNumber() : null);
                    row.put("fullName", s != null ? s.getFullName() : null);
                    row.put("totalLateDays", e.getValue());
                    return row;
                })
                .collect(Collectors.toList());
    }
}
