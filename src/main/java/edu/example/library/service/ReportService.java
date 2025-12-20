package edu.example.library.service;

import edu.example.library.model.BorrowRequest;
import edu.example.library.model.RequestStatus;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
public class ReportService {
    private final BorrowService borrowService;
    private final BookService bookService;
    private final AuthService authService;
    public ReportService(BorrowService bs, BookService bks, AuthService as){
        this.borrowService = bs; this.bookService = bks; this.authService = as;
    }
    public StudentReport studentReport(String username){
    List<BorrowRequest> reqs = borrowService.forStudent(username);

    // Only approved requests count as actual borrows (loans).
    List<BorrowRequest> approved = reqs.stream()
            .filter(r -> r.getStatus() == RequestStatus.APPROVED)
            .toList();

    long total = approved.size();
    long notReturned = approved.stream().filter(r -> r.getReceivedAt() == null).count();
    long delayed = approved.stream()
            .filter(r -> r.getReceivedAt() != null && r.getReceivedAt().isAfter(r.getTo()))
            .count();

    return new StudentReport(username, total, notReturned, delayed);
}
    public LibraryStats libraryStats(){
    Collection<BorrowRequest> reqs = borrowService.allRequests();

    int totalRequests = reqs.size();
    int totalApproved = (int) reqs.stream()
            .filter(r -> r.getStatus() == RequestStatus.APPROVED)
            .count();

    // Average days a book was borrowed = from (borrow start) until receivedAt (return date)
    double avgDays = reqs.stream()
            .filter(r -> r.getStatus() == RequestStatus.APPROVED && r.getReceivedAt() != null)
            .mapToLong(r -> Math.abs(ChronoUnit.DAYS.between(r.getFrom(), r.getReceivedAt())))
            .average()
            .orElse(0.0);

    return new LibraryStats(totalRequests, totalApproved, avgDays);
}
}
