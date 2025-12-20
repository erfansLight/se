package edu.example.library;

import edu.example.library.model.BorrowRequest;
import edu.example.library.service.AuthService;
import edu.example.library.service.BookService;
import edu.example.library.service.BorrowService;
import edu.example.library.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ReportServiceTest {
    private AuthService auth;
    private BookService books;
    private BorrowService borrow;
    private ReportService report;

    private final LocalDate TODAY = LocalDate.of(2025, 1, 10);

    @BeforeEach
    public void setup(){
        auth = new AuthService();
        books = new BookService();
        borrow = new BorrowService(books, auth);
        report = new ReportService(borrow, books, auth);

        auth.register("stu1","p");
        books.addBook("B","A",2020); // B001

        // Approved & returned on time (3 days)
        BorrowRequest r1 = borrow.createRequest("stu1","B001", TODAY, TODAY.plusDays(7));
        borrow.approve(r1.getId(), TODAY);
        borrow.receive(r1.getId(), TODAY.plusDays(3));

        // Pending request should not be counted as borrow in student report
        borrow.createRequest("stu1","B001", TODAY, TODAY.plusDays(7)); // book is AVAILABLE now due to receive()
    }

    // Scenario 4-1
    @Test
    public void studentReportCalculated(){
        var rep = report.studentReport("stu1");
        assertEquals(1, rep.getTotalBorrows());
        assertEquals(0, rep.getNotReturned());
        assertEquals(0, rep.getDelayed());
    }

    // Scenario 4-2
    @Test
    public void libraryStatsAvgDaysCalculated(){
        var s = report.libraryStats();
        assertEquals(2, s.getTotalRequests()); // 1 approved + 1 pending
        assertEquals(1, s.getTotalApproved());
        assertEquals(3.0, s.getAvgDays(), 0.0001);
    }
}
