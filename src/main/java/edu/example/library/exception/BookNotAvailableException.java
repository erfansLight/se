package edu.example.library.exception;
public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(String msg){ super(msg); }
}
