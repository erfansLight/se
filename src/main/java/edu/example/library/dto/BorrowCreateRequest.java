package edu.example.library.dto;

import jakarta.validation.constraints.NotNull;

public class BorrowCreateRequest {
    @NotNull
    private Long bookId;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}
