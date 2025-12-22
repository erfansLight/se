package edu.example.library.service;

import edu.example.library.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecifications {

    public static Specification<Book> titleContains(String title) {
        return (root, query, cb) -> title == null || title.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> authorContains(String author) {
        return (root, query, cb) -> author == null || author.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Book> yearEquals(Integer year) {
        return (root, query, cb) -> year == null
                ? cb.conjunction()
                : cb.equal(root.get("publishYear"), year);
    }
}
