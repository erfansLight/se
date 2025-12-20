package edu.example.library;

import edu.example.library.exception.BookNotAvailableException;
import edu.example.library.model.*;
import edu.example.library.service.LibraryStats;
import edu.example.library.service.StudentReport;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    @Test
    public void bookGettersAndSettersWork(){
        Book b = new Book("B001", "Title", "Author", 2020);
        assertEquals("B001", b.getId());
        assertEquals("Title", b.getTitle());
        assertEquals("Author", b.getAuthor());
        assertEquals(2020, b.getYear());
        assertEquals(BookStatus.AVAILABLE, b.getStatus());

        b.setTitle("T2");
        b.setAuthor("A2");
        b.setYear(2021);
        b.setStatus(BookStatus.BORROWED);

        assertEquals("T2", b.getTitle());
        assertEquals("A2", b.getAuthor());
        assertEquals(2021, b.getYear());
        assertEquals(BookStatus.BORROWED, b.getStatus());
    }

    @Test
    public void userActiveFlagAndRole(){
        User u = new User("u", "p", UserRole.STUDENT);
        assertTrue(u.isActive());
        assertEquals(UserRole.STUDENT, u.getRole());
        u.setActive(false);
        assertFalse(u.isActive());
    }

    @Test
    public void borrowRequestDefaultStatusPending(){
        LocalDate d = LocalDate.of(2025,1,1);
        BorrowRequest r = new BorrowRequest("R1","stu","B001", d, d.plusDays(7));
        assertEquals(RequestStatus.PENDING, r.getStatus());
        r.setStatus(RequestStatus.APPROVED);
        assertEquals(RequestStatus.APPROVED, r.getStatus());
        assertNull(r.getReceivedAt());
        r.setReceivedAt(d.plusDays(2));
        assertEquals(d.plusDays(2), r.getReceivedAt());
        assertTrue(r.toString().contains("R1"));
    }

    @Test
    public void statsObjectsHoldValues(){
        StudentReport sr = new StudentReport("stu", 2, 1, 1);
        assertEquals("stu", sr.getUsername());
        assertEquals(2, sr.getTotalBorrows());
        assertEquals(1, sr.getNotReturned());
        assertEquals(1, sr.getDelayed());

        LibraryStats ls = new LibraryStats(10, 5, 2.5);
        assertEquals(10, ls.getTotalRequests());
        assertEquals(5, ls.getTotalApproved());
        assertEquals(2.5, ls.getAvgDays(), 0.0001);
    }

    @Test
    public void exceptionCanBeCreated(){
        BookNotAvailableException ex = new BookNotAvailableException("x");
        assertEquals("x", ex.getMessage());
    }
}
