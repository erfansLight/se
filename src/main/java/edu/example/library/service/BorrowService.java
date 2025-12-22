package edu.example.library.service;

import edu.example.library.entity.*;
import edu.example.library.exception.ApiException;
import edu.example.library.repo.BookRepository;
import edu.example.library.repo.BorrowRequestRepository;
import edu.example.library.repo.StudentRepository;
import edu.example.library.repo.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowService {

    private final BorrowRequestRepository borrowRepo;
    private final StudentRepository studentRepo;
    private final BookRepository bookRepo;
    private final UserAccountRepository userRepo;

    public BorrowService(BorrowRequestRepository borrowRepo,
                         StudentRepository studentRepo,
                         BookRepository bookRepo,
                         UserAccountRepository userRepo) {
        this.borrowRepo = borrowRepo;
        this.studentRepo = studentRepo;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
    }

    private UserAccount currentUser() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !(a.getPrincipal() instanceof edu.example.library.config.UserPrincipal p)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication required");
        }
        return p.getUser();
    }

    private StudentProfile currentStudent() {
        UserAccount user = currentUser();
        if (user.getRole() != Role.STUDENT) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Student role required");
        }
        StudentProfile student = studentRepo.findByUserId(user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "STUDENT_NOT_FOUND", "Student profile not found"));
        if (!student.isActive()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "STUDENT_INACTIVE", "Student is inactive");
        }
        return student;
    }

    @Transactional
    public BorrowRequest createRequest(Long bookId) {
        StudentProfile student = currentStudent();
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND", "Book not found"));
        BorrowRequest br = new BorrowRequest(student, book);
        return borrowRepo.save(br);
    }

    public List<BorrowRequest> pendingRequests() {
        return borrowRepo.findByStatus(BorrowStatus.PENDING);
    }

    public BorrowRequest get(Long id) {
        return borrowRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "BORROW_NOT_FOUND", "Borrow request not found"));
    }

    @Transactional
    public BorrowRequest approve(Long requestId) {
        UserAccount staff = currentUser();
        if (staff.getRole() == Role.STUDENT) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Employee/Admin role required");
        }
        BorrowRequest br = get(requestId);
        if (br.getStatus() != BorrowStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_STATE", "Only pending requests can be approved");
        }
        Book book = br.getBook();
        if (book.getAvailableCopies() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "NO_COPIES", "No available copies for this book");
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepo.save(book);

        br.setStatus(BorrowStatus.APPROVED);
        br.setApprovedAt(Instant.now());
        br.setApprovedBy(staff);
        br.setDueDate(LocalDate.now().plusDays(14));
        return borrowRepo.save(br);
    }

    @Transactional
    public BorrowRequest reject(Long requestId, String reason) {
        UserAccount staff = currentUser();
        if (staff.getRole() == Role.STUDENT) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Employee/Admin role required");
        }
        BorrowRequest br = get(requestId);
        if (br.getStatus() != BorrowStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_STATE", "Only pending requests can be rejected");
        }
        br.setStatus(BorrowStatus.REJECTED);
        br.setRejectedBy(staff);
        br.setRejectionReason(reason);
        return borrowRepo.save(br);
    }

    @Transactional
    public BorrowRequest markReturned(Long borrowId) {
        UserAccount staff = currentUser();
        if (staff.getRole() == Role.STUDENT) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Employee/Admin role required");
        }
        BorrowRequest br = get(borrowId);
        if (br.getStatus() != BorrowStatus.APPROVED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_STATE", "Only approved borrows can be returned");
        }
        br.setStatus(BorrowStatus.RETURNED);
        br.setReturnedAt(Instant.now());
        br.setReturnedBy(staff);

        Book book = br.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepo.save(book);

        return borrowRepo.save(br);
    }

    public List<BorrowRequest> historyForStudent(Long studentId) {
        return borrowRepo.findByStudentIdOrderByRequestedAtDesc(studentId);
    }
}
