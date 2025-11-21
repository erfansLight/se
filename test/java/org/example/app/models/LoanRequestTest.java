package org.example.app.models;

import org.example.app.users.Employee;
import org.example.app.users.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanRequestTest {

    @BeforeEach
    void resetCounter() throws Exception {
        // Reset static counter using reflection so tests stay predictable
        Field f = LoanRequest.class.getDeclaredField("counter");
        f.setAccessible(true);
        f.setInt(null, 1);
    }

    @Test
    void testConstructorAndGetters() {
        Student s = new Student("sara", "1111", "سارا");
        Book b = new Book("OS", "Author", 2020);

        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 10);

        LoanRequest req = new LoanRequest(s, b, start, end);

        assertEquals(1, req.getId());
        assertEquals(s, req.getStudent());
        assertEquals(b, req.getBook());
        assertEquals(start, req.getStartDate());
        assertEquals(end, req.getEndDate());
        assertEquals(LoanStatus.PENDING, req.getStatus());
    }

    @Test
    void testApprove() {
        Student s = new Student("student", "1234", "Ali");
        Book b = new Book("Algo", "Writer", 2015);
        Employee emp = new Employee("emp", "pass", "Employee One");

        LoanRequest req = new LoanRequest(s, b,
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 2, 5));

        assertTrue(b.isAvailable());

        req.approve(emp);

        assertEquals(LoanStatus.APPROVED, req.getStatus());
        assertFalse(b.isAvailable()); // approving should set book unavailable
    }

    @Test
    void testReject() {
        Student s = new Student("user", "1", "Name");
        Book b = new Book("Book", "Writer", 2000);

        LoanRequest req = new LoanRequest(s, b,
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 10));

        req.reject();

        assertEquals(LoanStatus.REJECTED, req.getStatus());
        assertTrue(b.isAvailable()); // reject should NOT change book availability
    }

    @Test
    void testToString() {
        Student s = new Student("ali", "111", "Ali");
        Book b = new Book("Networks", "Author", 2010);

        LoanRequest req = new LoanRequest(
                s,
                b,
                LocalDate.of(2024, 4, 12),
                LocalDate.of(2024, 4, 20)
        );

        String out = req.toString();

        assertTrue(out.contains("درخواست 1"));
        assertTrue(out.contains("ali"));
        assertTrue(out.contains("Networks"));
        assertTrue(out.contains("2024-04-12"));
        assertTrue(out.contains("2024-04-20"));
        assertTrue(out.contains("PENDING"));
    }
}
