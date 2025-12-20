package edu.example.library;

import edu.example.library.model.Book;
import edu.example.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookServiceTest {
    private BookService bs;

    @BeforeEach
    public void setup(){
        bs = new BookService();
        bs.addBook("Java Basics","John",2015);
        bs.addBook("Advanced Topics","John",2015);
        bs.addBook("Misc","Alice",2010);
    }

    // Scenario 2-1
    @Test
    public void searchByTitle(){
        List<Book> r = bs.search("Java", null, null);
        assertFalse(r.isEmpty());
        assertTrue(r.stream().allMatch(b -> b.getTitle().contains("Java")));
    }

    // Scenario 2-2
    @Test
    public void searchByAuthorAndYear(){
        List<Book> r = bs.search(null, 2015, "John");
        assertEquals(2, r.size());
        assertTrue(r.stream().allMatch(b -> b.getAuthor().equals("John") && b.getYear()==2015));
    }

    // Scenario 2-3
    @Test
    public void searchAllWhenNull(){
        List<Book> r = bs.search(null, null, null);
        assertEquals(3, r.size());
    }

    // Scenario 2-4
    @Test
    public void searchNoMatchReturnsEmpty(){
        List<Book> r = bs.search("zzz", null, null);
        assertTrue(r.isEmpty());
    }
}
