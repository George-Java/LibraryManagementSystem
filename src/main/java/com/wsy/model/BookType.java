package com.wsy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookType {
    private String number;
    private String typeName;
    private Integer days;
    private Float fk;
}