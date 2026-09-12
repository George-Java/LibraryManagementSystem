package com.wsy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reader {
    private String barcode;
    private String name;
    private String sex;
    private Integer age;
    private String profession;
    private String type;
    private String identityCard;
    private Integer maxNum;
    private LocalDate date;
    private String phone;
    private Float keepMoney;
    private LocalDate dateOfIssuance;
}