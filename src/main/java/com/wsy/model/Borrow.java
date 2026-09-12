package com.wsy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Borrow {
    private Integer id;
    private String readerNumber;
    private String bookISBN;
    private Integer operator;
    private Integer isReturn;
    private LocalDate borrowDate;
    private LocalDate returnDate;
}