package edu.example.library.controller;

import edu.example.library.dto.BookUpsertRequest;
import edu.example.library.entity.Book;
import edu.example.library.service.BookService;
import edu.example.library.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // 2.1 لیست کتاب‌ها (جستجو و فیلتر با Query Params)
    @GetMapping("/api/books")
    public ApiResponse<List<Book>> list(@RequestParam(required = false) String title,
                                        @RequestParam(required = false) String author,
                                        @RequestParam(required = false) Integer year,
                                        @RequestParam(required = false) Boolean availableOnly,
                                        HttpServletRequest http) {
        return ApiResponse.ok(bookService.list(title, author, year, availableOnly), http.getRequestURI());
    }

    // 2.2 جزئیات کتاب
    @GetMapping("/api/books/{id}")
    public ApiResponse<Book> get(@PathVariable Long id, HttpServletRequest http) {
        return ApiResponse.ok(bookService.get(id), http.getRequestURI());
    }

    // 2.3 ایجاد کتاب جدید (کارمند)
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @PostMapping("/api/books")
    public ApiResponse<Book> create(@Valid @RequestBody BookUpsertRequest req, HttpServletRequest http) {
        return ApiResponse.ok(bookService.create(req), http.getRequestURI());
    }

    // 2.4 بروزرسانی کتاب (کارمند)
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @PutMapping("/api/books/{id}")
    public ApiResponse<Book> update(@PathVariable Long id,
                                    @Valid @RequestBody BookUpsertRequest req,
                                    HttpServletRequest http) {
        return ApiResponse.ok(bookService.update(id, req), http.getRequestURI());
    }

    // 2.5 جستجوی پیشرفته (عنوان/نویسنده/سال)
    @GetMapping("/api/books/search")
    public ApiResponse<List<Book>> search(@RequestParam(required = false) String title,
                                          @RequestParam(required = false) String author,
                                          @RequestParam(required = false) Integer year,
                                          HttpServletRequest http) {
        return ApiResponse.ok(bookService.list(title, author, year, null), http.getRequestURI());
    }
}
