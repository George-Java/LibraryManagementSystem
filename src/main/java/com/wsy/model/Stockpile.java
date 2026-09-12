package com.wsy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stockpile {
    private String bookISBN;
    private Integer stockQuantity;
}