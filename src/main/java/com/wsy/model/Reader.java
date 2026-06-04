package com.wsy.model;
import java.time.LocalDate;
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
    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public String getProfession() {
        return profession;
    }
    public void setProfession(String profession) {
        this.profession = profession;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getIdentityCard() {
        return identityCard;
    }
    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }
    public Integer getMaxNum() {
        return maxNum;
    }
    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public Float getKeepMoney() {
        return keepMoney;
    }
    public void setKeepMoney(Float keepMoney) {
        this.keepMoney = keepMoney;
    }
    public LocalDate getDateOfIssuance() {
        return dateOfIssuance;
    }
    public void setDateOfIssuance(LocalDate dateOfIssuance) {
        this.dateOfIssuance = dateOfIssuance;
    }
}