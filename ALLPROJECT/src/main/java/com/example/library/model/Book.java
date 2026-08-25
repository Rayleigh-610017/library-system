package com.example.library.model;

import java.io.Serializable;

public class Book implements Serializable {
    private String isbn;
    private String title;
    private String category;
    private String writer;
    private String publisher;
    private String url;
    private String rentStatus; // "Available" または "Rent"

    public Book() {}

    public Book(String isbn, String title, String category, String writer, String publisher, String url, String rentStatus) {
        this.isbn = isbn;
        this.title = title;
        this.category = category;
        this.writer = writer;
        this.publisher = publisher;
        this.url = url;
        this.rentStatus = rentStatus;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getWriter() { return writer; }
    public void setWriter(String writer) { this.writer = writer; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getRentStatus() { return rentStatus; }
    public void setRentStatus(String rentStatus) { this.rentStatus = rentStatus; }
}