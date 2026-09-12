package com.wsy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookInfo {
    private String bookISBN;
    private String category;
    private String bookname;
    private String writer;
    private String publisher;
    private String translator;
    private LocalDate date;
    private Double price;
}