package edu.example.library.service;

import edu.example.library.model.Book;
import edu.example.library.model.BookStatus;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class BookService {
    private final Map<String, Book> books = new LinkedHashMap<>();
    public BookService(){}
    private int counter = 1;
    public Book addBook(String title, String author, int year){
        String id = String.format("B%03d", counter++);
        Book b = new Book(id, title, author, year);
        books.put(id, b);
        return b;
    }
    public List<Book> search(String title, Integer year, String author){
        return books.values().stream().filter(b -> {
            boolean ok = true;
            if(title!=null && !title.isBlank()) ok = ok && b.getTitle().toLowerCase().contains(title.toLowerCase());
            if(year!=null) ok = ok && b.getYear()==year;
            if(author!=null && !author.isBlank()) ok = ok && b.getAuthor().toLowerCase().contains(author.toLowerCase());
            return ok;
        }).collect(Collectors.toList());
    }
    public List<Book> searchByTitleOnly(String title){
        return books.values().stream().filter(b -> b.getTitle().toLowerCase().contains(title.toLowerCase())).collect(Collectors.toList());
    }
    public Collection<Book> allBooks(){ return books.values(); }
    public Book getById(String id){ return books.get(id); }
    public void updateBook(String id, String title, String author, int year){
        Book b = books.get(id);
        if(b!=null){
            b.setTitle(title); b.setAuthor(author); b.setYear(year);
        }
    }
    public void setStatus(String id, BookStatus status){
        Book b = books.get(id);
        if(b!=null) b.setStatus(status);
    }
    public void seedBook(Book b){
        books.put(b.getId(), b);
    }
}
