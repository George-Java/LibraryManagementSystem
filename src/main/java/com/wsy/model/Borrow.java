package com.wsy.model;
import java.time.LocalDate;
public class Borrow {
    private Integer id;
    private String readerNumber;
    private String bookISBN;
    private Integer operator;
    private Integer isReturn;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getReaderNumber() {
        return readerNumber;
    }
    public void setReaderNumber(String readerNumber) {
        this.readerNumber = readerNumber;
    }
    public String getBookISBN() {
        return bookISBN;
    }
    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }
    public Integer getOperator() {
        return operator;
    }
    public void setOperator(Integer operator) {
        this.operator = operator;
    }
    public Integer getIsReturn() {
        return isReturn;
    }
    public void setIsReturn(Integer isReturn) {
        this.isReturn = isReturn;
    }
    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }
    public LocalDate getReturnDate() {
        return returnDate;
    }
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}