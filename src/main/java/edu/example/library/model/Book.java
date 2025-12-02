package edu.example.library.model;
public class Book {
    private final String id;
    private String title;
    private String author;
    private int year;
    private BookStatus status = BookStatus.AVAILABLE;
    public Book(String id, String title, String author, int year){
        this.id = id; this.title = title; this.author = author; this.year = year;
    }
    public String getId(){return id;}
    public String getTitle(){return title;}
    public String getAuthor(){return author;}
    public int getYear(){return year;}
    public BookStatus getStatus(){return status;}
    public void setStatus(BookStatus s){this.status = s;}
    public void setTitle(String t){this.title = t;}
    public void setAuthor(String a){this.author = a;}
    public void setYear(int y){this.year = y;}
    @Override public String toString(){ return String.format("%s | %s | %s | %d | %s", id, title, author, year, status); }
}
