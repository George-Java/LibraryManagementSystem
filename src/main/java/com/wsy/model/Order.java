package com.wsy.model;
import java.time.LocalDate;
public class Order {
    private Integer orderId;
    private String bookISBN;
    private LocalDate date;
    private Integer number;
    private Integer operator;
    private Integer checkAndAccept;
    private Float discount;
    public Integer getOrderId() {
        return orderId;
    }
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    public String getBookISBN() {
        return bookISBN;
    }
    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }
    public Integer getOperator() {
        return operator;
    }
    public void setOperator(Integer operator) {
        this.operator = operator;
    }
    public Integer getCheckAndAccept() {
        return checkAndAccept;
    }
    public void setCheckAndAccept(Integer checkAndAccept) {
        this.checkAndAccept = checkAndAccept;
    }
    public Float getDiscount() {
        return discount;
    }
    public void setDiscount(Float discount) {
        this.discount = discount;
    }
}