package com.wsy.model;
public class BookType {
    private String number;
    private String typeName;
    private Integer days;
    private Float fk;
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public Integer getDays() {
        return days;
    }
    public void setDays(Integer days) {
        this.days = days;
    }
    public Float getFk() {
        return fk;
    }
    public void setFk(Float fk) {
        this.fk = fk;
    }
}