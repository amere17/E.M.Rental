package com.example.emrental;

/**
 * User Class
 */
public class User {
    String userId;
    String FullName;
    String Paypal;
    String Mail;
    String Number;
    String rate;

    /**
     * empty c-tor
     */
    public User() {
    } // Empty constructor is required

    /**
     * c-tor
     *
     * @param mUserId   userId
     * @param mFullName full name
     * @param mPaypal   paypal email
     * @param mMail     email
     * @param mNumber   phone number
     * @param mRate     rate
     */
    public User(String mUserId, String mFullName, String mPaypal, String mMail, String mNumber, String mRate) {
        this.userId = mUserId;
        this.FullName = mFullName;
        this.Paypal = mPaypal;
        this.Mail = mMail;
        this.Number = mNumber;
        this.rate = mRate;
    }

    /**
     * c-tor
     *
     * @param mFullName name
     * @param mPaypal   paypal email
     * @param mMail     email
     * @param mNumber   number
     * @param mRate     rate
     */
    public User(String mFullName, String mPaypal, String mMail, String mNumber, String mRate) {
        this.FullName = mFullName;
        this.Paypal = mPaypal;
        this.Mail = mMail;
        this.Number = mNumber;
        this.rate = mRate;
    }

    /**
     * getter
     *
     * @return full name
     */
    public String getName() {
        return this.FullName;
    }

    /**
     * setter
     *
     * @param name name
     */
    public void setName(String name) {
        this.FullName = name;
    }

    /**
     * getter
     *
     * @return paypal email
     */
    public String getPaypal() {
        return this.Paypal;
    }

    /**
     * setter
     *
     * @param payPal pp email
     */
    public void setPaypal(String payPal) {
        this.Paypal = payPal;
    }

    /**
     * getter
     *
     * @return phone number
     */
    public String getNumber() {
        return Number;
    }

    /**
     * getter
     *
     * @return full name
     */
    public String getFullName() {
        return FullName;
    }

    /**
     * getter
     *
     * @return email
     */
    public String getMail() {
        return Mail;
    }

    /**
     * getter
     *
     * @return rate
     */
    public String getRate() {
        return rate;
    }

    /**
     * setter
     *
     * @param rate rate
     */
    public void setRate(String rate) {
        this.rate = rate;
    }

    /**
     * setter
     *
     * @param mail email
     */
    public void setMail(String mail) {
        Mail = mail;
    }

    /**
     * setter
     *
     * @param number number
     */
    public void setNumber(String number) {
        Number = number;
    }

    /**
     * getter
     *
     * @return userID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * setter
     *
     * @param userId userID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
