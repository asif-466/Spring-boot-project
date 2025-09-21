package com.example.login_app;

public class DtoSignupRequest {
    String name;
    String mobile;
    Double balance;
        public DtoSignupRequest(String name,String mobile,Double balance){
            this.name=name;
            this.mobile=mobile;
            this.balance=balance;
        }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
