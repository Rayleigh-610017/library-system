package com.example.library.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ReadingLog implements Serializable {
    private int logId;
    private int userId;
    private String isbn;
    private Timestamp rentDate;
    
    // 表示用の追加フィールド（JOIN用）
    private String bookTitle;

    public ReadingLog() {}

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Timestamp getRentDate() { return rentDate; }
    public void setRentDate(Timestamp rentDate) { this.rentDate = rentDate; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
}