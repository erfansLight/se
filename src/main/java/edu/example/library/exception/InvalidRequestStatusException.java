package edu.example.library.exception;
public class InvalidRequestStatusException extends RuntimeException {
    public InvalidRequestStatusException(String msg){ super(msg); }
}
