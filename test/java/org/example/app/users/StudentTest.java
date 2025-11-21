package org.example.app.users;

import org.example.app.models.Book;
import org.example.app.models.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @BeforeEach
    void resetLoanCounter() throws Exception {
        Field f = Loan.class.getDeclaredField("counter");
        f.setAccessible(true);
        f.setInt(null, 1);
    }

    @Test
    void testConstructor() {
        Student s = new Student("user1", "1234", "Ali");

        assertEquals("user1", s.getUsername());
        assertEquals("Ali", s.getName());
        assertTrue(s.isActive());
        assertTrue(s.getLoanHistory().isEmpty());
    }

    @Test
    void testActiveToggle() {
        Student s = new Student("user1", "1234", "Ali");

        assertTrue(s.isActive());
        s.setActive(false);
        assertFalse(s.isActive());
        s.setActive(true);
        assertTrue(s.isActive());
    }

    @Test
    void testAddLoan() {
        Student s = new Student("user1", "1234", "Ali");
        Book b = new Book("OS", "Author", 2001);

        Loan loan = new Loan(s, b, LocalDate.of(2024, 1, 1));
        s.addLoan(loan);

        assertEquals(1, s.getLoanHistory().size());
        assertEquals(loan, s.getLoanHistory().get(0));
    }
}
