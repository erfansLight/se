package edu.example.library;

import edu.example.library.model.BorrowRequest;
import edu.example.library.service.AuthService;
import edu.example.library.service.BookService;
import edu.example.library.service.BorrowService;
import edu.example.library.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class ReportServiceTest {
    private AuthService auth;
    private BookService books;
    private BorrowService borrow;
    private ReportService report;
    @BeforeEach public void setup(){
        auth = new AuthService();
        books = new BookService();
        borrow = new BorrowService(books, auth);
        report = new ReportService(borrow, books, auth);
        auth.register("stu1","p");
        books.addBook("B","A",2020);
        BorrowRequest r = borrow.createRequest("stu1","B001", LocalDate.now(), LocalDate.now().plusDays(3));
        borrow.approve(r.getId());
        borrow.receive(r.getId(), LocalDate.now().plusDays(1));
    }
    @Test public void studentReportCalculated(){
        var rep = report.studentReport("stu1");
        assertEquals(1, rep.getTotalBorrows());
    }
    @Test public void libraryStatsAvgDays(){
        var s = report.libraryStats();
        assertTrue(s.getAvgDays()>=0.0);
    }
}
