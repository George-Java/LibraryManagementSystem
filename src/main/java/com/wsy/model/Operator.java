package com.wsy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operator {
    private Integer id;
    private String name;
    private String sex;
    private Integer age;
    private String phone;
    private String identityCard;
    private LocalDate workDate;
    private Boolean admin;
    private String userName;
    private String password;

    public boolean isAdmin() {
        return Boolean.TRUE.equals(admin);
    }
}
