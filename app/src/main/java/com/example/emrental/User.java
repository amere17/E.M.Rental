package com.example.emrental;

public class User {
    String userId;
    String FullName;
    String Paypal;
    String Mail;
    String Number;
    String rate;


    public User() {
    } // Empty constructor is required

    public User(String mUserId, String mFullName, String mPaypal, String mMail, String mNumber, String mRate) {
        this.userId = mUserId;
        this.FullName = mFullName;
        this.Paypal = mPaypal;
        this.Mail = mMail;
        this.Number = mNumber;
        this.rate = mRate;
    }
    public User( String mFullName, String mPaypal, String mMail, String mNumber, String mRate) {
        this.FullName = mFullName;
        this.Paypal = mPaypal;
        this.Mail = mMail;
        this.Number = mNumber;
        this.rate = mRate;
    }


    public String getName() {
        return this.FullName;
    }

    public void setName(String name) {
        this.FullName = name;
    }

    public String getPaypal() {
        return this.Paypal;
    }

    public void setPaypal(String payPal) {
        this.Paypal = payPal;
    }

    public String getNumber() {
        return Number;
    }

    public String getFullName() {
        return FullName;
    }

    public String getMail() {
        return Mail;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
