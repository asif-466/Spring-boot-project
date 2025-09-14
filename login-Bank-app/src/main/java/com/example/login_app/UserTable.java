package com.example.login_app;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserTable {
    @Id
    private String mobile;
    private String name;
    private String password;
    private double balance;

    public UserTable() {
    }

    public UserTable(String name, String mobile, String password, Double balance) {
        this.name = name;
        this.mobile = mobile;
        this.password = password;
        this.balance=balance;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}

