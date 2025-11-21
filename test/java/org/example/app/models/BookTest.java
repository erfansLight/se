package org.example.app.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookTest {

    @BeforeEach
    void resetCounter() throws Exception {
        // Reset static counter using reflection so tests are predictable
        var field = Book.class.getDeclaredField("counter");
        field.setAccessible(true);
        field.setInt(null, 1);
    }

    @Test
    void testConstructorAndGetters() {
        Book book = new Book("Title", "Author", 2024);

        assertEquals(1, book.getId());
        assertEquals("Title", book.getTitle());
        assertEquals("Author", book.getAuthor());
        assertEquals(2024, book.getYear());
        assertTrue(book.isAvailable());
    }

    @Test
    void testSetters() {
        Book book = new Book("Old", "Old", 1990);

        book.setTitle("NewTitle");
        book.setAuthor("NewAuthor");
        book.setYear(2020);

        assertEquals("NewTitle", book.getTitle());
        assertEquals("NewAuthor", book.getAuthor());
        assertEquals(2020, book.getYear());
    }

    @Test
    void testAvailabilitySetter() {
        Book book = new Book("T", "A", 2000);

        assertTrue(book.isAvailable());

        book.setAvailable(false);
        assertFalse(book.isAvailable());
    }

    @Test
    void testToString() {
        Book book = new Book("Title", "Author", 2024);

        String output = book.toString();

        assertTrue(output.contains("1 - Title (Author, 2024)"));
        assertTrue(output.contains("موجود"));
    }
}
