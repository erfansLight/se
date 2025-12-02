package edu.example.library;

import edu.example.library.model.Book;
import edu.example.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class BookServiceTest {
    private BookService bs;
    @BeforeEach public void setup(){ bs = new BookService(); bs.addBook("Java Programming","John",2010); bs.addBook("Advanced Topics","John",2015); bs.addBook("Misc","Alice",2010); }
    @Test public void searchByTitle(){ List<Book> r = bs.search("Java", null, null); assertFalse(r.isEmpty()); }
    @Test public void searchByAuthorAndYear(){ List<Book> r = bs.search(null, 2015, "John"); assertEquals(1, r.size()); }
    @Test public void searchAllWhenNull(){ List<Book> r = bs.search(null, null, null); assertEquals(3, r.size()); }
    @Test public void searchNoMatchReturnsEmpty(){ List<Book> r = bs.search("zzz", null, null); assertTrue(r.isEmpty()); }
}
