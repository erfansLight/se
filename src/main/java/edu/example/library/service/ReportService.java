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
        long total = reqs.size();
        long notReturned = reqs.stream().filter(r-> r.getReceivedAt()==null).count();
        long delayed = reqs.stream().filter(r-> r.getReceivedAt()!=null && r.getReceivedAt().isAfter(r.getTo())).count();
        return new StudentReport(username, total, notReturned, delayed);
    }
    public LibraryStats libraryStats(){
        Collection<BorrowRequest> reqs = borrowService.allRequests();
        double avgDays = reqs.stream().filter(r-> r.getReceivedAt()!=null)
            .mapToLong(r-> ChronoUnit.DAYS.between(r.getReceivedAt(), r.getTo()))
            .map(Math::abs)
            .average().orElse(0.0);
        return new LibraryStats(reqs.size(), (int) reqs.stream().filter(r->r.getStatus()==RequestStatus.APPROVED).count(), avgDays);
    }
}
