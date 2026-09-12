package com.wsy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Integer orderId;
    private String bookISBN;
    private LocalDate date;
    private Integer number;
    private Integer operator;
    private Integer checkAndAccept;
    private Float discount;
}