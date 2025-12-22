package edu.example.library.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "borrow_requests")
public class BorrowRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private BorrowStatus status = BorrowStatus.PENDING;

    @Column(nullable = false)
    private Instant requestedAt = Instant.now();

    @Column
    private Instant approvedAt;

    @Column
    private LocalDate dueDate;

    @Column
    private Instant returnedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by_user_id")
    private UserAccount approvedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rejected_by_user_id")
    private UserAccount rejectedBy;

    @Column(length = 500)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "returned_by_user_id")
    private UserAccount returnedBy;

    public BorrowRequest() {
    }

    public BorrowRequest(StudentProfile student, Book book) {
        this.student = student;
        this.book = book;
        this.status = BorrowStatus.PENDING;
        this.requestedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public StudentProfile getStudent() {
        return student;
    }

    public void setStudent(StudentProfile student) {
        this.student = student;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public BorrowStatus getStatus() {
        return status;
    }

    public void setStatus(BorrowStatus status) {
        this.status = status;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Instant getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(Instant returnedAt) {
        this.returnedAt = returnedAt;
    }

    public UserAccount getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(UserAccount approvedBy) {
        this.approvedBy = approvedBy;
    }

    public UserAccount getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(UserAccount rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public UserAccount getReturnedBy() {
        return returnedBy;
    }

    public void setReturnedBy(UserAccount returnedBy) {
        this.returnedBy = returnedBy;
    }
}
