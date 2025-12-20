package edu.example.library;

import edu.example.library.exception.BookNotAvailableException;
import edu.example.library.exception.InvalidRequestStatusException;
import edu.example.library.exception.InvalidStudentStatusException;
import edu.example.library.model.BookStatus;
import edu.example.library.model.BorrowRequest;
import edu.example.library.model.RequestStatus;
import edu.example.library.service.AuthService;
import edu.example.library.service.BookService;
import edu.example.library.service.BorrowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BorrowServiceTest {
    private AuthService auth;
    private BookService books;
    private BorrowService borrow;

    private final LocalDate TODAY = LocalDate.of(2025, 1, 10);

    @BeforeEach
    public void setup(){
        auth = new AuthService();
        books = new BookService();
        borrow = new BorrowService(books, auth);

        auth.register("stu1","p");
        books.addBook("B","A",2020); // will be B001
    }

    // Scenario 3-1
    @Test
    public void activeStudentRequestsAvailableBookCreatesPendingRequest(){
        BorrowRequest r = borrow.createRequest("stu1","B001", TODAY, TODAY.plusDays(7));
        assertNotNull(r);
        assertEquals(RequestStatus.PENDING, r.getStatus());
    }

    // Scenario 3-2
    @Test
    public void inactiveStudentCannotRequest(){
        auth.register("stu2","p");
        auth.getUser("stu2").setActive(false);

        assertThrows(InvalidStudentStatusException.class,
                () -> borrow.createRequest("stu2","B001", TODAY, TODAY.plusDays(1)));
    }

    // Scenario 3-3
    @Test
    public void requestForBorrowedBookThrows(){
        BorrowRequest r1 = borrow.createRequest("stu1","B001", TODAY, TODAY.plusDays(7));
        borrow.approve(r1.getId(), TODAY);
        assertEquals(BookStatus.BORROWED, books.getById("B001").getStatus());

        assertThrows(BookNotAvailableException.class,
                () -> borrow.createRequest("stu1","B001", TODAY, TODAY.plusDays(7)));
    }

    // Scenario 3-4
    @Test
    public void approveValidRequestChangesStatuses(){
        BorrowRequest r = borrow.createRequest("stu1","B001", TODAY, TODAY.plusDays(7));
        borrow.approve(r.getId(), TODAY);

        assertEquals(RequestStatus.APPROVED, r.getStatus());
        assertEquals(BookStatus.BORROWED, books.getById("B001").getStatus());
    }

    // Scenario 3-5
    @Test
    public void approveAlreadyApprovedThrows(){
        BorrowRequest r = borrow.createRequest("stu1","B001", TODAY, TODAY.plusDays(7));
        borrow.approve(r.getId(), TODAY);
        assertThrows(InvalidRequestStatusException.class, () -> borrow.approve(r.getId(), TODAY));
    }

    @Test
    public void approveStartDateNotTodayOrYesterdayThrows(){
        BorrowRequest r = borrow.createRequest("stu1","B001", TODAY.plusDays(2), TODAY.plusDays(7));
        assertThrows(InvalidRequestStatusException.class, () -> borrow.approve(r.getId(), TODAY));
    }

    @Test
    public void receiveReturnsBookToAvailable(){
        BorrowRequest r = borrow.createRequest("stu1","B001", TODAY, TODAY.plusDays(7));
        borrow.approve(r.getId(), TODAY);
        borrow.receive(r.getId(), TODAY.plusDays(3));

        assertEquals(BookStatus.AVAILABLE, books.getById("B001").getStatus());
        assertEquals(TODAY.plusDays(3), r.getReceivedAt());
    }

    @Test
    public void receiveTwiceThrows(){
        BorrowRequest r = borrow.createRequest("stu1","B001", TODAY, TODAY.plusDays(7));
        borrow.approve(r.getId(), TODAY);
        borrow.receive(r.getId(), TODAY.plusDays(3));
        assertThrows(InvalidRequestStatusException.class, () -> borrow.receive(r.getId(), TODAY.plusDays(4)));
    }
}
