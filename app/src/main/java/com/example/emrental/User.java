package com.example.emrental;

public class User {
    String userId;
    String FullName;
    String Paypal;
    String Mail;
    String Number;


    public User() {} // Empty constructor is required
    public User(String mUserId, String mFullName,String mPaypal,String mMail,String mNumber) {
        this.userId = mUserId;
        this.FullName = mFullName;
        this.Paypal = mPaypal;
        this.Mail = mMail;
        this.Number = mNumber;
    }

    public String getTooltId() { return this.userId; }
    public void setToolId(String toolId) { this.userId = toolId; }
    public String getName() { return this.FullName; }
    public void setName(String name) { this.FullName = name; }
    public String getPaypal() { return this.Paypal;}
    public void setPaypal(String payPal) { this.Paypal = payPal; }

}
