package org.example.app.models;

public class Book {
    private static int counter = 1;
    private final int id;
    private String title;
    private String author;
    private int year;
    private boolean available = true;

    public Book(String title, String author, int year) {
        this.id = counter++;
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() {
        return String.format("%d - %s (%s, %d) - %s", id, title, author, year, available ? "موجود" : "امانت داده شده");
    }
}
