package edu.example.library;

import edu.example.library.model.BorrowRequest;
import edu.example.library.model.RequestStatus;
import edu.example.library.service.AuthService;
import edu.example.library.service.BookService;
import edu.example.library.service.BorrowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
public class BorrowServiceTest {
    private AuthService auth;
    private BookService books;
    private BorrowService borrow;
    @BeforeEach public void setup(){
        auth = new AuthService();
        books = new BookService();
        borrow = new BorrowService(books, auth);
        auth.register("stu1","p");
        books.addBook("Book A","A",2020);
    }
    @Test public void activeStudentCreatesPending(){
        BorrowRequest r = borrow.createRequest("stu1","B001", LocalDate.now(), LocalDate.now().plusDays(7));
        assertEquals(RequestStatus.PENDING, r.getStatus());
    }
    @Test public void inactiveStudentThrows(){
        auth.register("stu2","p"); auth.getUser("stu2").setActive(false);
        assertThrows(Exception.class, ()-> borrow.createRequest("stu2","B001", LocalDate.now(), LocalDate.now().plusDays(1)));
    }
    @Test public void bookAlreadyBorrowedThrows(){
        BorrowRequest r = borrow.createRequest("stu1","B001", LocalDate.now(), LocalDate.now().plusDays(7));
        borrow.approve(r.getId());
        assertThrows(Exception.class, ()-> borrow.createRequest("stu1","B001", LocalDate.now(), LocalDate.now().plusDays(7)));
    }
    @Test public void approveValidChangesStatus(){
        BorrowRequest r = borrow.createRequest("stu1","B001", LocalDate.now(), LocalDate.now().plusDays(7));
        borrow.approve(r.getId());
        assertEquals(RequestStatus.APPROVED, r.getStatus());
    }
}
