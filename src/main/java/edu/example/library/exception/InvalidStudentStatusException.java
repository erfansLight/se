package edu.example.library.exception;
public class InvalidStudentStatusException extends RuntimeException {
    public InvalidStudentStatusException(String msg){ super(msg); }
}
