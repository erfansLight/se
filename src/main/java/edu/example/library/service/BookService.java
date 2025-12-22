package edu.example.library.service;

import edu.example.library.dto.BookUpsertRequest;
import edu.example.library.entity.Book;
import edu.example.library.exception.ApiException;
import edu.example.library.repo.BookRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepo;

    public BookService(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    public List<Book> list(String title, String author, Integer year, Boolean availableOnly) {
        Specification<Book> spec = Specification.where(BookSpecifications.titleContains(title))
                .and(BookSpecifications.authorContains(author))
                .and(BookSpecifications.yearEquals(year));

        List<Book> books = bookRepo.findAll(spec);
        if (availableOnly != null && availableOnly) {
            books.removeIf(b -> b.getAvailableCopies() <= 0);
        }
        return books;
    }

    public Book get(Long id) {
        return bookRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND", "Book not found"));
    }

    @Transactional
    public Book create(BookUpsertRequest req) {
        Book b = new Book(req.getTitle(), req.getAuthor(), req.getPublishYear(), req.getIsbn(), req.getTotalCopies());
        return bookRepo.save(b);
    }

    @Transactional
    public Book update(Long id, BookUpsertRequest req) {
        Book b = get(id);
        int delta = req.getTotalCopies() - b.getTotalCopies();
        b.setTitle(req.getTitle());
        b.setAuthor(req.getAuthor());
        b.setPublishYear(req.getPublishYear());
        b.setIsbn(req.getIsbn());
        b.setTotalCopies(req.getTotalCopies());
        // keep availability consistent (simple approach): adjust availableCopies by delta, but not below 0.
        b.setAvailableCopies(Math.max(0, b.getAvailableCopies() + delta));
        return bookRepo.save(b);
    }
}
