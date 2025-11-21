package org.example.app.models;

import org.example.app.users.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanTest {

    @BeforeEach
    void resetCounter() throws Exception {
        Field f = Loan.class.getDeclaredField("counter");
        f.setAccessible(true);
        f.setInt(null, 1);
    }

    @Test
    void testConstructorAndGetters() {
        Student s = new Student("sara", "1234", "سارا");
        Book b = new Book("Title", "Author", 2023);

        Loan loan = new Loan(s, b, LocalDate.of(2024, 1, 1));

        assertEquals(1, loan.getId());
        assertEquals(s, loan.getStudent());
        assertEquals(b, loan.getBook());
        assertEquals(LocalDate.of(2024, 1, 1), loan.getBorrowDate());
        assertNull(loan.getReturnDate());
        assertFalse(loan.isReturned());
    }

    @Test
    void testReturnDateAndAvailability() {
        Student s = new Student("ali", "2222", "علی");
        Book b = new Book("OS", "Writer", 2000);

        Loan loan = new Loan(s, b, LocalDate.of(2024, 5, 10));

        assertTrue(b.isAvailable()); // initially true

        // simulate borrowing: book becomes unavailable
        b.setAvailable(false);

        loan.setReturnDate(LocalDate.of(2024, 5, 20));

        assertEquals(LocalDate.of(2024, 5, 20), loan.getReturnDate());
        assertTrue(loan.isReturned());
        assertTrue(b.isAvailable()); // must be true after return
    }

    @Test
    void testToString() {
        Student s = new Student("sara", "123", "سارا");
        Book b = new Book("Algo", "Author", 2015);

        Loan loan = new Loan(s, b, LocalDate.of(2024, 2, 2));

        String text = loan.toString();

        assertTrue(text.contains("وام 1"));
        assertTrue(text.contains("sara"));
        assertTrue(text.contains("Algo"));
        assertTrue(text.contains("2024-02-02"));
        assertTrue(text.contains("نامشخص")); // because not returned yet
    }
}
